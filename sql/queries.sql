--------------------------------------------------------------------------------
-- MODIFYING DATA
--------------------------------------------------------------------------------

-- 1. Add SPA
INSERT INTO cd.facilities (facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
VALUES (9, 'Spa', 20, 30, 100000, 800);

-- 2. Add a new Spa with auto-generated facid
INSERT INTO cd.facilities (facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
SELECT (SELECT MAX(facid) FROM cd.facilities) + 1, 'Spa', 20, 30, 100000, 800;

-- 3. Fix the mistake 
UPDATE cd.facilities
SET initialoutlay = 10000
WHERE name = 'Tennis Court 2';

-- 4. Change price of the second tennis court 
UPDATE cd.facilities
SET membercost = (SELECT membercost * 1.1 FROM cd.facilities WHERE name = 'Tennis Court 1'),
    guestcost = (SELECT guestcost * 1.1 FROM cd.facilities WHERE name = 'Tennis Court 1')
WHERE name = 'Tennis Court 2';

-- 5. Delete all bookings from the bookings table
DELETE FROM cd.bookings;

-- 6. Remove member 37 (only if they have no bookings)
DELETE FROM cd.members
WHERE memid = 37 AND memid NOT IN (SELECT memid FROM cd.bookings);

--------------------------------------------------------------------------------
-- BASICS
--------------------------------------------------------------------------------

-- 7. Facilities  fee less than 1/50th 
SELECT facid, name, membercost, monthlymaintenance
FROM cd.facilities
WHERE membercost > 0 AND membercost < (monthlymaintenance / 50.0);

-- 8. List all facilities with the'Tennis' 
SELECT * FROM cd.facilities WHERE name LIKE '%Tennis%';

-- 9.  List  facilities with ID 1 and 5
SELECT * FROM cd.facilities WHERE facid IN (1, 5);

-- 10. List members who joined after the start of September 2012
SELECT memid, surname, firstname, joindate
FROM cd.members
WHERE joindate >= '2012-09-01'
ORDER BY joindate ASC;

-- 11. Combined list of all surnames and facility 
SELECT surname FROM cd.members
UNION
SELECT name FROM cd.facilities;


--------------------------------------------------------------------------------
-- JOINS
--------------------------------------------------------------------------------

-- 12. Retrieve start times for a specific member (David Farrell)
SELECT bks.starttime
FROM cd.bookings bks
JOIN cd.members mems ON bks.memid = mems.memid
WHERE mems.firstname = 'David' AND mems.surname = 'Farrell'
ORDER BY bks.starttime;

-- 13. Find booking start times for tennis courts on a specific date
SELECT bks.starttime, facs.name
FROM cd.bookings bks
JOIN cd.facilities facs ON bks.facid = facs.facid
WHERE facs.name LIKE 'Tennis Court%'
  AND bks.starttime >= '2012-09-21' AND bks.starttime < '2012-09-22'
ORDER BY bks.starttime;

-- 14. Output all members including their recommender (if available)
SELECT mems.firstname AS memfname, mems.surname AS memsname, recs.firstname AS recfname, recs.surname AS recsname
FROM cd.members mems
LEFT JOIN cd.members recs ON recs.memid = mems.recommendedby
ORDER BY mems.surname, mems.firstname;

-- 15. Identify members who have recommended at least one other person
SELECT DISTINCT recs.firstname, recs.surname
FROM cd.members mems
JOIN cd.members recs ON recs.memid = mems.recommendedby
ORDER BY recs.surname, recs.firstname;

-- 16. List recommenders without using a JOIN (Subquery method)
SELECT DISTINCT mems.firstname || ' ' || mems.surname AS member,
    (SELECT recs.firstname || ' ' || recs.surname FROM cd.members recs WHERE recs.memid = mems.recommendedby) AS recommender
FROM cd.members mems
ORDER BY member;

--------------------------------------------------------------------------------
-- AGGREGATION
--------------------------------------------------------------------------------

-- 17. Compute the count of recommendations per member
SELECT recommendedby, COUNT(*)
FROM cd.members
WHERE recommendedby IS NOT NULL
GROUP BY recommendedby
ORDER BY recommendedby;

-- 18. Calculate total slot usage per facility
SELECT facid, SUM(slots) AS total_slots
FROM cd.bookings
GROUP BY facid
ORDER BY facid;

-- 19. Calculate total slots per facility for a specific month (Sept 2012)
SELECT facid, SUM(slots) AS total_slots
FROM cd.bookings
WHERE starttime >= '2012-09-01' AND starttime < '2012-10-01'
GROUP BY facid
ORDER BY total_slots;

-- 20. Calculate total slots per facility per month for the year 2012
SELECT facid, EXTRACT(MONTH FROM starttime) AS month, SUM(slots) AS total_slots
FROM cd.bookings
WHERE EXTRACT(YEAR FROM starttime) = 2012
GROUP BY facid, month
ORDER BY facid, month;

-- 21. Find the count of unique members who have booked at least once
SELECT COUNT(DISTINCT memid) FROM cd.bookings;

-- 22. Find the first booking date for each member after Sept 1st, 2012
SELECT mems.surname, mems.firstname, mems.memid, MIN(bks.starttime) AS starttime
FROM cd.members mems
JOIN cd.bookings bks ON mems.memid = bks.memid
WHERE bks.starttime >= '2012-09-01'
GROUP BY mems.surname, mems.firstname, mems.memid
ORDER BY mems.memid;

-- 23. List members with a count of total members attached to each row (Window function)
SELECT COUNT(*) OVER(), firstname, surname
FROM cd.members
ORDER BY joindate;

-- 24. Generate a sequential row number for members based on join date
SELECT ROW_NUMBER() OVER(ORDER BY joindate), firstname, surname
FROM cd.members
ORDER BY joindate;

-- 25. Find the facility with the highest total slots booked (Rank function)
SELECT facid, total
FROM (
    SELECT facid, SUM(slots) AS total, RANK() OVER (ORDER BY SUM(slots) DESC) AS rank
    FROM cd.bookings
    GROUP BY facid
) AS ranked_facs
WHERE rank = 1;

--------------------------------------------------------------------------------
-- STRING OPERATIONS
--------------------------------------------------------------------------------

-- 26. Concatenate surname and firstname
SELECT surname || ', ' || firstname AS formatted_name
FROM cd.members;

-- 27. Find telephone numbers containing parentheses (Regex match)
SELECT memid, telephone
FROM cd.members
WHERE telephone ~ '[()]'
ORDER BY memid;

-- 28. Count distribution of members by the first letter of their surname
SELECT LEFT(surname, 1) AS letter, COUNT(*) AS count
FROM cd.members
GROUP BY letter
ORDER BY letter;
