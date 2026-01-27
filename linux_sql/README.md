# Linux Cluster Monitoring Agent

## Introduction
This is my Linux Cluster Monitoring project

I built this tool to practice my skills in **Linux**, **Bash Scripting**, **Docker**, and **PostgreSQL**.

The goal of this project was to create a  agent that can run on any Linux server, collect hardware informations (like CPU and Memory) and usage data, and save everything in a database.

**What I used to build this:**
* **Linux (CentOS/Rocky):** The operating system where everything runs.
* **Bash Scripts:** To automate the data collection.
* **PostgreSQL:** To store all the data.
* **Docker:** To easily access  the database without installing it directly on my machine.
* **Git:** To track all the changes in my work and manage versions.

## Quick Start
If you want to try running this project yourself, here are the steps I used to get it up and running.

**1. Start the Database**
I used a script to create a Docker container running PostgreSQL.
```bash
# Create and start the container
./scripts/psql_docker.sh create db_password

2. Set up the Tables This command runs my SQL file to create the host_info and host_usage tables.

Bash

psql -h localhost -U postgres -d host_agent -f sql/ddl.sql
3. Register a Host This script runs once to save the hardware specs (static data) to the database.

Bash

# Usage: ./scripts/host_info.sh psql_host psql_port db_name psql_user psql_password
./scripts/host_info.sh localhost 5432 host_agent postgres db_password
4. Collect Usage Data This script checks the current memory and CPU usage. You can run it manually to test it:

Bash

./scripts/host_usage.sh localhost 5432 host_agent postgres db_password
5. Automate with Crontab To make it run every minute automatically, I added this line to my crontab (crontab -e):

Bash

* * * * * bash /full/path/to/linux_sql/scripts/host_usage.sh localhost 5432 host_agent postgres db_password > /tmp/host_usage.log 2>&1
Implementation
Architecture
To visualize how the pieces fit together, I drew this diagram. It shows how the Linux servers (agents) send data to the central Database.

Scripts
Here is a breakdown of the scripts I wrote and what they do:

psql_docker.sh: A utility script to control the Docker container. It lets me start, stop, or create the database with one command.

Bash

./scripts/psql_docker.sh start|stop|create [password]
host_info.sh: This script runs lscpu to get hardware details. It only needs to run once when a new server is set up.

Bash

./scripts/host_info.sh localhost 5432 host_agent postgres password
host_usage.sh: This is the main monitoring script. It uses vmstat to check how much memory and CPU is being used right now, then saves it to the DB.

Bash

./scripts/host_usage.sh localhost 5432 host_agent postgres password
crontab: This isn't a script file, but a tool I used to schedule host_usage.sh to run every single minute.

Bash

# Edited using: crontab -e
* * * * * bash /path/to/host_usage.sh ...
queries.sql: This file contains SQL queries that solve business problems for the LCA team. For example:

High CPU Detection: Identify which server is under heavy load (e.g., CPU usage > 90%).

Resource Planning: Calculate the average memory usage over 5-minute intervals to see if we need to upgrade our RAM.

Database Modeling
I decided to split the data into two tables to avoid repeating information.

1. host_info Stores data that doesn't change often (like CPU model). | Column | Description | | --- | --- | | id | Unique ID for the server | | hostname | The name of the machine | | cpu_number | Number of cores | | cpu_architecture | e.g., x86_64 | | cpu_model | e.g., Intel Xeon | | cpu_mhz | Speed of the CPU | | l2_cache | Cache size | | total_mem | Total RAM | | timestamp | When it was registered |

2. host_usage Stores data that changes every minute. | Column | Description | | --- | --- | | timestamp | When the data was collected | | host_id | Links back to the host_info table | | memory_free | Free RAM in MB | | cpu_idle | % of CPU doing nothing | | cpu_kernel | % of CPU running system tasks | | disk_io | Disk activity | | disk_available | Free space on the disk |

Test
I tested this project manually on a Linux VM.

Bash Testing: I ran the scripts and used echo to print variables to make sure lscpu and vmstat were being parsed correctly.

Database Check: I used psql to query the tables (SELECT * FROM host_usage) and confirmed that the data was actually being saved.

Automation: I watched the logs and the database for a few minutes to ensure crontab was successfully adding a new row every 60 seconds.

Deployment
Database: Deployed inside a Docker container using the postgres image.

Source Code: Managed via GitHub.

Scheduling: Uses the native Linux cron service (Crontab) to automate the data collection.

Improvements
Since this is an MVP (Minimum Viable Product), there are a few things I would improve if I had more time:

Handle Hardware Updates: Right now, if I upgrade the RAM, the script might fail because the hostname already exists. I should update the logic to handle changes.

Alerts: It would be cool to add a feature that sends me an email if the CPU usage gets too high (like over 90%).

Dashboard: Reading SQL tables is hard. I would like to connect this database to a tool like Grafana to see the usage data on a graph.
