# Introduction

This project is about building a database for a country club. The club has members, facilities, and bookings. The goal of the project is to organize all this information so the club can see how many people are using the courts and how much money they are making, it can also auxiliate the managers to decide action to improve the club and help the clients.

To build this, I used several important tools:

PostgreSQL: This is what holds all the data.

Docker: This helps run the database in a clean way on any computer.

Git: I used Git to save my work and track my progress, the history and changes.

Bash: I used simple commands to set everything up.

SQL: This is the language I used to talk to the database and get answers to questions.

# Table Setup (DDL)
The following SQL statements were used to create the schema and tables based on the provided data model.

```sql
CREATE SCHEMA cd;

CREATE TABLE cd.facilities (
    facid INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    membercost NUMERIC NOT NULL,
    guestcost NUMERIC NOT NULL,
    initialoutlay NUMERIC NOT NULL,
    monthlymaintenance NUMERIC NOT NULL,
    CONSTRAINT facilities_pk PRIMARY KEY (facid)
);

CREATE TABLE cd.members (
    memid INTEGER NOT NULL,
    surname VARCHAR(200) NOT NULL,
    firstname VARCHAR(200) NOT NULL,
    address VARCHAR(300) NOT NULL,
    zipcode INTEGER NOT NULL,
    telephone VARCHAR(20) NOT NULL,
    recommendedby INTEGER,
    joindate TIMESTAMP NOT NULL,
    CONSTRAINT members_pk PRIMARY KEY (memid),
    CONSTRAINT fk_members_recommendedby FOREIGN KEY (recommendedby) REFERENCES cd.members(memid)
);

CREATE TABLE cd.bookings (
    bookid INTEGER NOT NULL,
    facid INTEGER NOT NULL,
    memid INTEGER NOT NULL,
    starttime TIMESTAMP NOT NULL,
    slots INTEGER NOT NULL,
    CONSTRAINT bookings_pk PRIMARY KEY (bookid),
    CONSTRAINT fk_bookings_facid FOREIGN KEY (facid) REFERENCES cd.facilities(facid),
    CONSTRAINT fk_bookings_memid FOREIGN KEY (memid) REFERENCES cd.members(memid)
);


```

# Queries 

## Modifying Data

-- 1. Add a new facility (Spa)
```sql
INSERT INTO cd.facilities (facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
VALUES (9, 'Spa', 20, 30, 100000, 800);
```
-- 2. Add a new Spa with auto-generated facid
```sql
INSERT INTO cd.facilities (facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
SELECT (SELECT MAX(facid) FROM cd.facilities) + 1, 'Spa', 20, 30, 100000, 800;
```
-- 3. Fix the mistake for the second tennis court
```sql
UPDATE cd.facilities
SET initialoutlay = 10000
WHERE name = 'Tennis Court 2';
```

-- 4. Change the price of the second tennis court
``` 
UPDATE cd.facilities
SET membercost = (
        SELECT membercost * 1.1 
        FROM cd.facilities 
        WHERE name = 'Tennis Court 1'
    ),
    guestcost = (
        SELECT guestcost * 1.1 
        FROM cd.facilities 
        WHERE name = 'Tennis Court 1'
    )
WHERE name = 'Tennis Court 2';
```

-- 5. Delete all bookings from the bookings table
```sql
DELETE FROM cd.bookings;
```

-- 6. Remove member 37  if they have no bookings
```sql
DELETE FROM cd.members
WHERE memid = 37
  AND memid NOT IN (SELECT memid FROM cd.bookings);
```

## Basics

-- 7. Facilities with member fee less than 1/50
```sql
SELECT facid, name, membercost, monthlymaintenance
FROM cd.facilities
WHERE membercost > 0
  AND membercost < (monthlymaintenance / 50.0);
```

-- 8. List all facilities with the word 'Tennis'
```sql
SELECT *
FROM cd.facilities
WHERE name LIKE '%Tennis%';
```

-- 9. List details of facilities with ID 1 and 5
```sql
SELECT *
FROM cd.facilities
WHERE facid IN (1, 5);
```

-- 10. List members who joined after the start of September 2012
```sql
SELECT memid, surname, firstname, joindate
FROM cd.members
WHERE joindate >= '2012-09-01'
ORDER BY joindate;
```

-- 11. Produce a combined list of all surnames and facility names 
```sql
SELECT surname FROM cd.members
UNION
SELECT name FROM cd.facilities;
```

## Joins

-- 12. List start times for member David Farrell
```sql
SELECT bks.starttime
FROM cd.bookings bks
JOIN cd.members mems
  ON bks.memid = mems.memid
WHERE mems.firstname = 'David'
  AND mems.surname = 'Farrell'
ORDER BY bks.starttime;
```

-- 13. Find booking start times for tennis courts on a specific date
```sql
SELECT bks.starttime, facs.name
FROM cd.bookings bks
JOIN cd.facilities facs
  ON bks.facid = facs.facid
WHERE facs.name LIKE 'Tennis Court%'
  AND bks.starttime >= '2012-09-21'
  AND bks.starttime < '2012-09-22'
ORDER BY bks.starttime;
```

-- 14. Output all members including their recommender 
```sql
SELECT
    mems.firstname AS memfname,
    mems.surname   AS memsname,
    recs.firstname AS recfname,
    recs.surname   AS recsname
FROM cd.members mems
LEFT JOIN cd.members recs
  ON recs.memid = mems.recommendedby
ORDER BY mems.surname, mems.firstname;
```


-- 15. Identify members who have recommended at least one other person
```sql
SELECT DISTINCT recs.firstname, recs.surname AS fullname
FROM cd.members mems
JOIN cd.members recs
  ON recs.memid = mems.recommendedby
ORDER BY recs.surname, recs.firstname;
```

-- 16. List recommenders without using a JOIN 
```sql
SELECT DISTINCT
    mems.firstname || ' ' || mems.surname AS member,
    (
        SELECT recs.firstname || ' ' || recs.surname
        FROM cd.members recs
        WHERE recs.memid = mems.recommendedby
    ) AS recommender
FROM cd.members mems
ORDER BY member;
```

## Agregation

-- 17. Compute the count of recommendations per member
```sql
SELECT recommendedby, COUNT(*)
FROM cd.members
WHERE recommendedby IS NOT NULL
GROUP BY recommendedby
ORDER BY recommendedby;
```

-- 18. Calculate total slot usage per facility
```sql
SELECT facid, SUM(slots) AS total_slots
FROM cd.bookings
GROUP BY facid
ORDER BY facid;
```

-- 19. Calculate total slots per facility for Sept 2012
```sql
SELECT facid, SUM(slots) AS total_slots
FROM cd.bookings
WHERE starttime >= '2012-09-01'
  AND starttime < '2012-10-01'
GROUP BY facid
ORDER BY total_slots;
```

-- 20. Calculate total slots per facility per month for 2012
```sql
SELECT
    facid,
    EXTRACT(MONTH FROM starttime) AS month,
    SUM(slots) AS total_slots
FROM cd.bookings
WHERE EXTRACT(YEAR FROM starttime) = 2012
GROUP BY facid, month
ORDER BY facid, month;
```

-- 21. Count of unique members who booked at least once
```sql
SELECT COUNT(DISTINCT memid)
FROM cd.bookings;
```
-- 22. First booking date for each member after Sept 1st, 2012
```sql
SELECT
    mems.surname,
    mems.firstname,
    mems.memid,
    MIN(bks.starttime) AS starttime
FROM cd.members mems
JOIN cd.bookings bks
  ON mems.memid = bks.memid
WHERE bks.starttime >= '2012-09-01'
GROUP BY mems.surname, mems.firstname, mems.memid
ORDER BY mems.memid;
```

-- 23. Total members count shown on each row (Window function)
```sql
SELECT
    COUNT(*) OVER() AS total_members,
    firstname,
    surname
FROM cd.members
ORDER BY joindate;
```

-- 24. Sequential row number based on join date
```sql
SELECT
    ROW_NUMBER() OVER (ORDER BY joindate) AS row_num,
    firstname,
    surname
FROM cd.members
ORDER BY joindate;
```

-- 25. Facility with highest total slots booked (Rank)
```sql
SELECT facid, total
FROM (
    SELECT
        facid,
        SUM(slots) AS total,
        RANK() OVER (ORDER BY SUM(slots) DESC) AS rank
    FROM cd.bookings
    GROUP BY facid
) AS ranked_facs
WHERE rank = 1;
```

## String operations

-- 26. Concatenate surname and firstname
```sql
SELECT surname || ', ' || firstname AS formatted_name
FROM cd.members;
```

```
-- 27. Telephone numbers with regex
```sql
SELECT memid, telephone
FROM cd.members
WHERE telephone ~ '[()]'
ORDER BY memid;
```

-- 28. Distribution of members by first letter of surname
```sql
SELECT
    LEFT(surname, 1) AS letter,
    COUNT(*) AS count
FROM cd.members
GROUP BY letter
ORDER BY letter;
```
