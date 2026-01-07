import requests, zipfile, io, os, csv, re
from pathlib import Path

DATA_DIR = Path('./data')
OUT_DIR = Path('./output')
OUT_DIR.mkdir(parents=True, exist_ok=True)
def download_and_unzip(url, out_dir):
    out_dir = Path(out_dir)
    out_dir.mkdir(parents=True, exist_ok=True)
    r = requests.get(url, stream=True, timeout=60)
    r.raise_for_status()
    z = zipfile.ZipFile(io.BytesIO(r.content))
    z.extractall(out_dir)
    return [out_dir / name for name in z.namelist()]

def find_file(folder: Path, pattern: str):
    rx = re.compile(pattern, re.IGNORECASE)

    # handle an iterable/list of extracted paths
    if isinstance(folder, (list, tuple, set)):
        for item in folder:
            p = Path(item)
            if p.exists() and rx.search(p.name):
                return p
        return None

    # handle a folder Path
    folder = Path(folder)
    for p in folder.rglob('*.csv'):
        if rx.search(p.name):
            return p
    return None

def load_csv_with_preamble(path):
    text = Path(path).read_text(encoding='utf-8-sig', errors='ignore').splitlines()
    if not text:
        return [], []

    header_idx = None
    # Heuristics: look for a line that contains country-related headers or year tokens
    for i, line in enumerate(text):
        if not line.strip():
            continue
        try:
            fields = next(csv.reader([line]))
        except Exception:
            continue
        norm = [f.strip().strip('"') for f in fields]
        low_join = ' '.join([f.lower() for f in norm if f])
        # header likely contains "country" and "country code" or contains year columns
        if 'country code' in low_join:
            header_idx = i
            break
        if len(norm) >= 4 and any(re.match(r'^\d{4}$', t) for t in norm):
            header_idx = i
            break

    # fallback: find first line containing a year-like token
    if header_idx is None:
        for i, line in enumerate(text):
            if re.search(r'\b(19\d{2}|20\d{2})\b', line):
                header_idx = i
                break

    if header_idx is None:
        header_idx = 0

    lines = text[header_idx:]
    cleaned = []
    for line in lines:
        if not line.strip():
            continue
        low = line.strip().lower()
        cleaned.append(line)

    if not cleaned:
        return [], []

    # parse header fields and subsequent rows using csv.reader to preserve proper splitting
    try:
        header_fields = next(csv.reader([cleaned[0]]))
    except Exception:
        header_fields = [h.strip() for h in cleaned[0].split(',')]

    rows = []
    for row in csv.reader(cleaned[1:]):
        # skip completely empty rows
        if not any(cell.strip() for cell in row):
            continue
        # pad or trim to match header length
        if len(row) < len(header_fields):
            row += [''] * (len(header_fields) - len(row))
        elif len(row) > len(header_fields):
            row = row[:len(header_fields)]
        rows.append(dict(zip(header_fields, row)))

    return rows, header_fields

def sql_escape(s):
    if s is None: return 'NULL'
    s = str(s).strip()
    if s == '' or s.upper() == 'NA': return 'NULL'
    # numeric?
    try:
        float(s.replace(',',''))
        return s.replace(',','')  # numeric literal
    except:
        return "'" + s.replace("'", "''") + "'"

def gen_countries_sql(meta_rows, out_path):
    # Map common fields to table columns
    cols = ['country_code','name','region','income_group','special_notes']
    with open(out_path, 'w', encoding='utf-8') as f:
        f.write('-- generated COUNTRY inserts\n')
        for r in meta_rows:
            # keys vary; try multiple possible header names
            code = r.get('Country Code')
            name = r.get('TableName')
            region = r.get('Region')
            income = r.get('IncomeGroup')
            notes = r.get('SpecialNotes')

            values = [sql_escape(code), sql_escape(name), sql_escape(region), sql_escape(income), sql_escape(notes)]
            f.write(f"INSERT INTO COUNTRY ({', '.join(cols)}) VALUES ({', '.join(values)});\n")


def gen_timeseries_sql(data_rows, out_path, table_name):
    print('data_rows count:', len(data_rows))
    # Expect columns: Country Name, Country Code, Indicator Name, Indicator Code, 1960, 1961, ...
    with open(out_path, 'w', encoding='utf-8') as f:
        f.write(f'-- generated {table_name} inserts\n')
        for r in data_rows:
            code = r.get('Country Code')
            if not code: continue
            for k, v in r.items():
                if re.match(r'^\d{4}$', k.strip()):
                    if v is None or v == '' or v.upper() == 'NA': continue
                    val = v.replace(',', '')
                    year = k.strip()
                    # use numeric value or NULL
                    try:
                        float(val)
                        f.write(f"INSERT INTO {table_name} (country_code, data_year, {table_name}) VALUES ('{code}', {year}, {val});\n")
                    except:
                        f.write(f"INSERT INTO {table_name} (country_code, data_year, {table_name}) VALUES ('{code}', {year}, NULL);\n")

def main():
    data_dir = Path('./data')
    out_dir = Path('./output')
    out_dir.mkdir(parents=True, exist_ok=True)

    urls = {
        'gdp': 'https://api.worldbank.org/v2/en/indicator/NY.GDP.MKTP.CD?downloadformat=csv',
        'pop': 'https://api.worldbank.org/v2/en/indicator/SP.POP.TOTL?downloadformat=csv'
    }

    all_extracted = []
    for key, url in urls.items():
        print('Downloading', key)
        files = download_and_unzip(url, data_dir)
        all_extracted += files

    # find metadata country CSV for GDP
    meta_pattern = r"Metadata_Country_API.*\.csv"
    meta_file = find_file(all_extracted, meta_pattern)
    if not meta_file:
        raise SystemExit('Metadata country CSV for GDP not found')
    meta_rows, _ = load_csv_with_preamble(meta_file)
    gen_countries_sql(meta_rows, out_dir / 'countries.sql')
    print('Wrote', out_dir / 'countries.sql')

    # GDP data file
    gdp_pattern = r'^(?!.*Metadata).*API_NY\.GDP\.MKTP.*\.csv'
    gdp_file = find_file(all_extracted, gdp_pattern)
    if not gdp_file:
        raise SystemExit('GDP data CSV not found')
    gdp_rows, _ = load_csv_with_preamble(gdp_file)
    gen_timeseries_sql(gdp_rows, out_dir / 'gdp.sql', 'GDP')
    print('Wrote', out_dir / 'gdp.sql')

    # Population data file
    pop_pattern = r'^(?!.*Metadata).*API_SP\.POP\.TOTL.*\.csv$'
    pop_file = find_file(all_extracted, pop_pattern)
    if not pop_file:
        raise SystemExit('Population data CSV not found')
    pop_rows, _ = load_csv_with_preamble(pop_file)
    gen_timeseries_sql(pop_rows, out_dir / 'population.sql', 'POPULATION')
    print('Wrote', out_dir / 'population.sql')

if __name__ == '__main__':
    main()
