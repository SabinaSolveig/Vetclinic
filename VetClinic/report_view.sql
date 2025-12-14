-- SQL запрос для создания VIEW "Отчет о приемах за период"
-- Этот VIEW группирует данные о приемах по сотрудникам и клиентам
-- VIEW можно использовать для фильтрации по периоду в запросах

CREATE OR REPLACE VIEW visits_report_view AS
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

-- Пример запроса для получения отчета за период с использованием VIEW:
-- SELECT 
--     EmployeeID,
--     EmployeeName,
--     ClientID,
--     ClientName,
--     SUM(VisitCount) AS VisitCount,
--     SUM(TotalCost) AS TotalCost
-- FROM visits_report_view
-- WHERE VisitDate >= '2024-01-01' AND VisitDate <= '2024-01-31'
-- GROUP BY EmployeeID, EmployeeName, ClientID, ClientName
-- ORDER BY EmployeeID, ClientID;

-- Пример запроса для получения итоговых данных по сотрудникам за период:
-- SELECT 
--     EmployeeID,
--     EmployeeName,
--     SUM(VisitCount) AS TotalVisits,
--     SUM(TotalCost) AS TotalCost,
--     COUNT(DISTINCT ClientID) AS UniqueClients
-- FROM visits_report_view
-- WHERE VisitDate >= '2024-01-01' AND VisitDate <= '2024-01-31'
-- GROUP BY EmployeeID, EmployeeName
-- ORDER BY EmployeeID;

