-- SQL скрипт для создания VIEW "Отчет по реализованным товарам и услугам"
-- Этот VIEW объединяет данные об услугах и товарах из приемов врачей

-- VIEW для детализированного отчета
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

-- VIEW для итоговых данных по врачам и типам
CREATE OR REPLACE VIEW products_services_report_summary_view AS
SELECT 
    e.EmployeeID,
    e.LastName || ' ' || e.FirstName || COALESCE(' ' || e.MiddleName, '') AS EmployeeName,
    'Услуга' AS ItemType,
    SUM(vs.Quantity) AS TotalQuantity,
    COALESCE(SUM(vs.SumWithDiscount), 0) AS TotalCost
FROM VisitServices vs
INNER JOIN Visits v ON vs.VisitID = v.VisitID
INNER JOIN Employees e ON v.EmployeeID = e.EmployeeID
GROUP BY 
    e.EmployeeID,
    e.LastName,
    e.FirstName,
    e.MiddleName

UNION ALL

SELECT 
    e.EmployeeID,
    e.LastName || ' ' || e.FirstName || COALESCE(' ' || e.MiddleName, '') AS EmployeeName,
    'Товар' AS ItemType,
    SUM(vp.Quantity) AS TotalQuantity,
    COALESCE(SUM(vp.Sum), 0) AS TotalCost
FROM VisitProducts vp
INNER JOIN Visits v ON vp.VisitID = v.VisitID
INNER JOIN Employees e ON v.EmployeeID = e.EmployeeID
GROUP BY 
    e.EmployeeID,
    e.LastName,
    e.FirstName,
    e.MiddleName;

-- Примеры использования VIEW:

-- 1. Получить отчет за период с фильтрацией по сотруднику и типу
-- SELECT 
--     EmployeeID,
--     EmployeeName,
--     ItemType,
--     ItemName,
--     SUM(Quantity) AS Quantity,
--     SUM(Cost) AS Cost
-- FROM products_services_report_view
-- WHERE VisitDate >= '2024-01-01' AND VisitDate <= '2024-01-31'
--     AND EmployeeID = 1  -- опционально
--     AND ItemType = 'Услуга'  -- опционально
-- GROUP BY EmployeeID, EmployeeName, ItemType, ItemID, ItemName
-- ORDER BY EmployeeName, ItemType, ItemName;

-- 2. Получить итоговые данные за период
-- SELECT 
--     EmployeeID,
--     EmployeeName,
--     ItemType,
--     SUM(TotalQuantity) AS TotalQuantity,
--     SUM(TotalCost) AS TotalCost
-- FROM products_services_report_summary_view
-- GROUP BY EmployeeID, EmployeeName, ItemType
-- ORDER BY EmployeeName, ItemType;

