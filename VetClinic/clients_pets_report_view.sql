-- SQL скрипт для создания VIEW "Отчет по клиентам и их питомцам"
-- Этот VIEW объединяет данные о клиентах, питомцах, приемах, услугах и товарах

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
        THEN ROUND(COALESCE(SUM(v.TotalCost), 0) / COUNT(DISTINCT v.VisitID), 2)
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

-- Примеры использования VIEW:

-- 1. Получить отчет за период с фильтрацией
-- SELECT 
--     ClientID,
--     ClientName,
--     PetID,
--     PetName,
--     SpeciesName,
--     BreedName,
--     SUM(VisitCount) AS VisitCount,
--     SUM(TotalCost) AS TotalCost,
--     CASE 
--         WHEN SUM(VisitCount) > 0 
--         THEN SUM(TotalCost) / SUM(VisitCount)
--         ELSE 0 
--     END AS AverageCheck,
--     SUM(ServicesCount) AS ServicesCount,
--     SUM(ProductsCount) AS ProductsCount,
--     CASE 
--         WHEN SUM(VisitCount) > 0 
--         THEN ROUND(AVG(PaidVisitsPercent), 2)
--         ELSE 0 
--     END AS PaidVisitsPercent
-- FROM clients_pets_report_view
-- WHERE VisitDate >= '2024-01-01' AND VisitDate <= '2024-01-31'
--     AND ClientID = 1  -- опционально
--     AND SpeciesName = 'Собака'  -- опционально
-- GROUP BY ClientID, ClientName, PetID, PetName, SpeciesName, BreedName
-- ORDER BY ClientName, PetName;

