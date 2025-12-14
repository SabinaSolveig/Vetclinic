-- Перечисления
CREATE TYPE appointment_status AS ENUM ('Scheduled', 'Completed', 'Cancelled', 'NoShow');

-- Справочники
CREATE TABLE IF NOT EXISTS Species (
    SpeciesID SERIAL PRIMARY KEY,
    SpeciesName VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS Breeds (
    BreedID SERIAL PRIMARY KEY,
    SpeciesID INT NOT NULL REFERENCES Species(SpeciesID) ON DELETE CASCADE,
    BreedName VARCHAR(100) NOT NULL,
    UNIQUE(SpeciesID, BreedName)
);

CREATE TABLE IF NOT EXISTS Specializations (
    SpecializationID SERIAL PRIMARY KEY,
    SpecializationName VARCHAR(100) NOT NULL UNIQUE,
    Description TEXT
);

CREATE TABLE IF NOT EXISTS ContactTypes (
    ContactTypeID SERIAL PRIMARY KEY,
    ContactTypeName VARCHAR(50) NOT NULL UNIQUE,
    Description TEXT
);

CREATE TABLE IF NOT EXISTS ServiceCategories (
    ServiceCategoryID SERIAL PRIMARY KEY,
    CategoryName VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS ProductCategories (
    ProductCategoryID SERIAL PRIMARY KEY,
    CategoryName VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS Services (
    ServiceID SERIAL PRIMARY KEY,
    ServiceName VARCHAR(150) NOT NULL,
    ServiceCategoryID INT REFERENCES ServiceCategories(ServiceCategoryID) ON DELETE SET NULL,
    Price NUMERIC(10,2) NOT NULL CHECK (Price >= 0),
    Description TEXT,
    UNIQUE(ServiceCategoryID, ServiceName)
);

CREATE TABLE IF NOT EXISTS Products (
    ProductID SERIAL PRIMARY KEY,
    ProductName VARCHAR(150) NOT NULL,
    ProductCategoryID INT REFERENCES ProductCategories(ProductCategoryID) ON DELETE SET NULL,
    Price NUMERIC(10,2) NOT NULL CHECK (Price >= 0),
    StockQuantity INT NOT NULL DEFAULT 0 CHECK (StockQuantity >= 0),
    UNIQUE(ProductCategoryID, ProductName)
);

CREATE TABLE IF NOT EXISTS PaymentMethods (
    PaymentMethodID SERIAL PRIMARY KEY,
    PaymentMethodName VARCHAR(50) NOT NULL UNIQUE,
    Description TEXT
);

-- Основные таблицы

CREATE TABLE IF NOT EXISTS Clients (
    ClientID SERIAL PRIMARY KEY,
    LastName VARCHAR(50) NOT NULL,
    FirstName VARCHAR(50) NOT NULL,
    MiddleName VARCHAR(50),
    Address VARCHAR(255),
    DiscountPercent NUMERIC(5,2) NOT NULL DEFAULT 0 CHECK (DiscountPercent >=0 AND DiscountPercent <=100),
    Notes TEXT
);

CREATE TABLE IF NOT EXISTS Employees (
    EmployeeID SERIAL PRIMARY KEY,
    LastName VARCHAR(50) NOT NULL,
    FirstName VARCHAR(50) NOT NULL,
    MiddleName VARCHAR(50),
    BirthDate DATE,
    SpecializationID INT REFERENCES Specializations(SpecializationID) ON DELETE SET NULL,
    HireDate DATE NOT NULL DEFAULT CURRENT_DATE,
    DismissalDate DATE,
    Active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS Pets (
    PetID SERIAL PRIMARY KEY,
    ClientID INT NOT NULL REFERENCES Clients(ClientID) ON DELETE CASCADE,
    Name VARCHAR(100) NOT NULL,
    SpeciesID INT REFERENCES Species(SpeciesID) ON DELETE SET NULL,
    BreedID INT REFERENCES Breeds(BreedID) ON DELETE SET NULL,
    BirthDate DATE,
    Age INT CHECK (Age >= 0),
    Gender CHAR(1) CHECK (Gender IN ('M','F')),
    Notes TEXT,
    UNIQUE(ClientID, Name, SpeciesID, BreedID)
);

CREATE TABLE IF NOT EXISTS ContactInfo (
    ContactID SERIAL PRIMARY KEY,
    OwnerType VARCHAR(10) NOT NULL CHECK (OwnerType IN ('Client','Employee')),
    OwnerID INT NOT NULL,
    ContactTypeID INT REFERENCES ContactTypes(ContactTypeID) ON DELETE CASCADE,
    ContactValue VARCHAR(255) NOT NULL,
    IsPrimary BOOLEAN DEFAULT FALSE,
    Notes TEXT,
    UNIQUE (OwnerType, OwnerID, ContactTypeID, ContactValue)
);

-- Предварительные записи с несколькими услугами

CREATE TABLE IF NOT EXISTS Appointments (
    AppointmentID SERIAL PRIMARY KEY,
    ClientID INT NOT NULL REFERENCES Clients(ClientID) ON DELETE CASCADE,
    PetID INT NOT NULL REFERENCES Pets(PetID) ON DELETE CASCADE,
    EmployeeID INT REFERENCES Employees(EmployeeID) ON DELETE SET NULL,
    AppointmentDate DATE NOT NULL,
    AppointmentTime TIME NOT NULL,
    Status appointment_status DEFAULT 'Scheduled',
    Notes TEXT
);

CREATE TABLE IF NOT EXISTS PreliminaryServiceSets (
    PreliminaryServiceSetID SERIAL PRIMARY KEY,
    AppointmentID INT NOT NULL REFERENCES Appointments(AppointmentID) ON DELETE CASCADE,
    ServiceID INT NOT NULL REFERENCES Services(ServiceID) ON DELETE CASCADE,
    Quantity INT NOT NULL DEFAULT 1 CHECK (Quantity > 0),
    Notes TEXT,
    UNIQUE(AppointmentID, ServiceID)
);

-- Посещения врачей и детали

CREATE TABLE IF NOT EXISTS Visits (
    VisitID SERIAL PRIMARY KEY,
    AppointmentID INT REFERENCES Appointments(AppointmentID) ON DELETE SET NULL,
    ClientID INT NOT NULL REFERENCES Clients(ClientID) ON DELETE CASCADE,
    PetID INT NOT NULL REFERENCES Pets(PetID) ON DELETE CASCADE,
    EmployeeID INT NOT NULL REFERENCES Employees(EmployeeID) ON DELETE SET NULL,
    VisitDate DATE NOT NULL,
    StartTime TIME,
    EndTime TIME,
    Diagnosis TEXT,
    Anamnesis TEXT,
    Treatment TEXT,
    Recommendations TEXT,
    TotalCost NUMERIC(10,2) DEFAULT 0 CHECK (TotalCost >= 0)
);

CREATE TABLE IF NOT EXISTS VisitServices (
    VisitID INT NOT NULL REFERENCES Visits(VisitID) ON DELETE CASCADE,
    ServiceID INT NOT NULL REFERENCES Services(ServiceID) ON DELETE SET NULL,
    Quantity INT NOT NULL DEFAULT 1 CHECK (Quantity > 0),
    Price NUMERIC(10,2) NOT NULL CHECK (Price >= 0),
    Sum NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (Sum >= 0),
    DiscountSum NUMERIC(10,2) DEFAULT 0 CHECK (DiscountSum >= 0),
    SumWithDiscount NUMERIC(10,2) GENERATED ALWAYS AS (GREATEST(Sum - DiscountSum, 0)) STORED,
    PRIMARY KEY(VisitID, ServiceID)
);

CREATE TABLE IF NOT EXISTS VisitProducts (
    VisitID INT NOT NULL REFERENCES Visits(VisitID) ON DELETE CASCADE,
    ProductID INT NOT NULL REFERENCES Products(ProductID) ON DELETE SET NULL,
    Quantity INT NOT NULL DEFAULT 1 CHECK (Quantity>0),
    Price NUMERIC(10,2) NOT NULL CHECK (Price>=0),
    Sum NUMERIC(10,2) GENERATED ALWAYS AS (Quantity * Price) STORED,
    PRIMARY KEY(VisitID, ProductID)
);

CREATE TABLE IF NOT EXISTS MaterialsUsed (
    VisitID INT NOT NULL REFERENCES Visits(VisitID) ON DELETE CASCADE,
    ProductID INT NOT NULL REFERENCES Products(ProductID) ON DELETE SET NULL,    
    Quantity NUMERIC(10,2) NOT NULL CHECK (Quantity>0),
    PRIMARY KEY(VisitID, ProductID)
);

CREATE TABLE IF NOT EXISTS Payments (
    PaymentID SERIAL PRIMARY KEY,
    VisitID INT NOT NULL REFERENCES Visits(VisitID) ON DELETE CASCADE,
    PaymentDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    Amount NUMERIC(10,2) NOT NULL CHECK (Amount>=0),
    PaymentMethodID INT REFERENCES PaymentMethods(PaymentMethodID) ON DELETE SET NULL,
    Status VARCHAR(50) DEFAULT 'Pending',
    Notes TEXT
);

-- Индексы

CREATE INDEX IF NOT EXISTS idx_pets_clientid ON Pets(ClientID);
CREATE INDEX IF NOT EXISTS idx_visits_clientid ON Visits(ClientID);
CREATE INDEX IF NOT EXISTS idx_visits_petid ON Visits(PetID);
CREATE INDEX IF NOT EXISTS idx_visits_employeeid ON Visits(EmployeeID);
CREATE INDEX IF NOT EXISTS idx_visitservices_serviceid ON VisitServices(ServiceID);
CREATE INDEX IF NOT EXISTS idx_visitproducts_productid ON VisitProducts(ProductID);
CREATE INDEX IF NOT EXISTS idx_prelim_services_appointmentid ON PreliminaryServiceSets(AppointmentID);

-- VIEW


CREATE OR REPLACE VIEW visit_report_view AS
SELECT 
    v.VisitDate,
    e.EmployeeID,
    e.LastName || ' ' || e.FirstName || COALESCE(' ' || e.MiddleName, '') AS EmployeeName,
    c.ClientID,
    c.LastName || ' ' || c.FirstName || COALESCE(' ' || c.MiddleName, '') AS ClientName,
    COUNT(DISTINCT v.VisitID) AS VisitCount,
    COALESCE(SUM(v.TotalCost), 0) AS TotalCost,
    CASE 
        WHEN COUNT(CASE WHEN v.StartTime IS NOT NULL AND v.EndTime IS NOT NULL THEN 1 END) > 0 
        THEN TO_CHAR(
            (DATE '2000-01-01' + AVG(EXTRACT(EPOCH FROM (v.EndTime - v.StartTime))) * INTERVAL '1 second'), 
            'HH24:MI' 
        ) 
        ELSE NULL 
    END AS AverageVisitDuration
FROM Visits v
INNER JOIN Employees e ON v.EmployeeID = e.EmployeeID
INNER JOIN Clients c ON v.ClientID = c.ClientID
GROUP BY 
    v.VisitDate,
    e.EmployeeID,
    e.LastName,
    e.FirstName,
    e.MiddleName,
    c.ClientID,
    c.LastName,
    c.FirstName,
    c.MiddleName;


CREATE OR REPLACE VIEW products_services_report_view AS
SELECT 
    e.EmployeeID,
    e.LastName || ' ' || e.FirstName || COALESCE(' ' || e.MiddleName, '') AS EmployeeName,
    'Услуга' AS ItemType,
    s.ServiceID AS ItemID,
    s.ServiceName AS ItemName,
    SUM(vs.Quantity) AS Quantity,
    COALESCE(SUM(vs.SumWithDiscount), 0) AS Cost,
    v.VisitDate
FROM VisitServices vs
INNER JOIN Visits v ON vs.VisitID = v.VisitID
INNER JOIN Employees e ON v.EmployeeID = e.EmployeeID
INNER JOIN Services s ON vs.ServiceID = s.ServiceID
GROUP BY 
    e.EmployeeID,
    e.LastName,
    e.FirstName,
    e.MiddleName,
    s.ServiceID,
    s.ServiceName,
    v.VisitDate

UNION ALL

SELECT 
    e.EmployeeID,
    e.LastName || ' ' || e.FirstName || COALESCE(' ' || e.MiddleName, '') AS EmployeeName,
    'Товар' AS ItemType,
    p.ProductID AS ItemID,
    p.ProductName AS ItemName,
    SUM(vp.Quantity) AS Quantity,
    COALESCE(SUM(vp.Sum), 0) AS Cost,
    v.VisitDate
FROM VisitProducts vp
INNER JOIN Visits v ON vp.VisitID = v.VisitID
INNER JOIN Employees e ON v.EmployeeID = e.EmployeeID
INNER JOIN Products p ON vp.ProductID = p.ProductID
GROUP BY 
    e.EmployeeID,
    e.LastName,
    e.FirstName,
    e.MiddleName,
    p.ProductID,
    p.ProductName,
    v.VisitDate;

CREATE OR REPLACE VIEW clients_pets_report_view AS
SELECT 
    c.ClientID,
    c.LastName || ' ' || c.FirstName || COALESCE(' ' || c.MiddleName, '') AS ClientName,
    p.PetID,
    p.Name AS PetName,
    s.SpeciesName,
    b.BreedName,
    v.VisitDate,
    COUNT(DISTINCT v.VisitID) AS VisitCount,
    COALESCE(SUM(v.TotalCost), 0) AS TotalCost,
    CASE 
        WHEN COUNT(DISTINCT v.VisitID) > 0 
        THEN COALESCE(SUM(v.TotalCost), 0) / COUNT(DISTINCT v.VisitID)
        ELSE 0 
    END AS AverageCheck,
    COALESCE(SUM(vs.Quantity), 0) AS ServicesCount,
    COALESCE(SUM(vp.Quantity), 0) AS ProductsCount,
    CASE 
        WHEN COUNT(DISTINCT v.VisitID) > 0 
        THEN ROUND(
            COUNT(DISTINCT CASE WHEN EXISTS (SELECT 1 FROM Payments WHERE VisitID = v.VisitID) THEN v.VisitID END)::NUMERIC / 
            COUNT(DISTINCT v.VisitID)::NUMERIC * 100, 
            2
        )
        ELSE 0 
    END AS PaidVisitsPercent
FROM Clients c
INNER JOIN Pets p ON c.ClientID = p.ClientID
LEFT JOIN Visits v ON p.PetID = v.PetID
LEFT JOIN VisitServices vs ON v.VisitID = vs.VisitID
LEFT JOIN VisitProducts vp ON v.VisitID = vp.VisitID
LEFT JOIN Species s ON p.SpeciesID = s.SpeciesID
LEFT JOIN Breeds b ON p.BreedID = b.BreedID
GROUP BY 
    c.ClientID,
    c.LastName,
    c.FirstName,
    c.MiddleName,
    p.PetID,
    p.Name,
    s.SpeciesName,
    b.BreedName,
    v.VisitDate;

-- Триггеры
-- Обновление возраста питомца
CREATE OR REPLACE FUNCTION update_pet_age() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.BirthDate IS NOT NULL THEN
        NEW.Age := DATE_PART('year', AGE(CURRENT_DATE, NEW.BirthDate));
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_pet_age
BEFORE INSERT OR UPDATE ON Pets
FOR EACH ROW
EXECUTE FUNCTION update_pet_age();

-- Пересчет Sum в VisitServices
CREATE OR REPLACE FUNCTION recalc_visitservices_sum() RETURNS TRIGGER AS $$
BEGIN
    NEW.Sum := NEW.Quantity * NEW.Price;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_recalc_visitservices_sum
BEFORE INSERT OR UPDATE ON VisitServices
FOR EACH ROW
EXECUTE FUNCTION recalc_visitservices_sum();

-- Пересчет суммы со скидкой DiscountSum клиента
CREATE OR REPLACE FUNCTION recalc_visitservices_discount() RETURNS TRIGGER AS $$
DECLARE
    client_discount NUMERIC := 0;
BEGIN
    SELECT DiscountPercent INTO client_discount
    FROM Clients c
    JOIN Visits v ON v.ClientID = c.ClientID
    WHERE v.VisitID = NEW.VisitID;

    NEW.DiscountSum := NEW.Sum * (client_discount / 100);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_recalc_visitservices_discount
BEFORE INSERT OR UPDATE ON VisitServices
FOR EACH ROW
EXECUTE FUNCTION recalc_visitservices_discount();

-- Пересчет TotalCost в таблице посещений Visits
CREATE OR REPLACE FUNCTION update_visit_total() RETURNS TRIGGER AS $$
DECLARE
    total_services NUMERIC := 0;
    total_products NUMERIC := 0;
BEGIN
    SELECT COALESCE(SUM(SumWithDiscount),0) INTO total_services
    FROM VisitServices
    WHERE VisitID = NEW.VisitID;

    SELECT COALESCE(SUM(Sum),0) INTO total_products
    FROM VisitProducts
    WHERE VisitID = NEW.VisitID;

    UPDATE Visits
    SET TotalCost = total_services + total_products
    WHERE VisitID = NEW.VisitID;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_visit_total_services
AFTER INSERT OR UPDATE OR DELETE ON VisitServices
FOR EACH ROW
EXECUTE FUNCTION update_visit_total();

CREATE TRIGGER trg_update_visit_total_products
AFTER INSERT OR UPDATE OR DELETE ON VisitProducts
FOR EACH ROW
EXECUTE FUNCTION update_visit_total();
