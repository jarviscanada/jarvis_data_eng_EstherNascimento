# Linux Cluster Monitoring Agent

## Introduction

This project is a Linux Cluster Monitoring tool designed to collect hardware specifications and real-time resource usage from multiple servers. I built this tool to practice and demonstrate skills in **Linux**, **Bash Scripting**, **Docker**, and **PostgreSQL**.

The goal of this project was to create an agent that can run on any Linux server, collect hardware information (like CPU and RAM) and usage data, and save everything in a centralized database.

## Quick Start

If you want to try  this project yourself, here are the steps to get it up and running:

### 1. Start the Database

Use the provided script to create a PostgreSQL container using Docker.bash
./scripts/psql_docker.sh create db_password

### 2. Set up the Tables

This command runs the SQL file to create the host_info and host_usage tables.bash
psql -h localhost -U postgres -d host_agent -f sql/ddl.sql

### 3. Register a Host

This script runs once to save the hardware specifications for your computer (static data) to the database.

Bash

./scripts/host_info.sh localhost 5432 host_agent postgres db_password

### 4. Collect Usage Data

This script checks the current memory and CPU usage. You can run it manually to test it:

Bash

./scripts/host_usage.sh localhost 5432 host_agent postgres db_password

### 5. Automate with Crontab

To make it run every minute automatically, add this line to your crontab (crontab -e):

Bash

* * * * * bash /path/to/host_usage.sh localhost 5432 host_agent postgres db_password > /tmp/host_usage.log 2>&1

## Implementation

## Architecture

Database Modeling
I decided to split the data into two tables to avoid repeating information and ensure data integrity. This way its easier to see and to understand.

host_info: Stores static data like CPU model and total RAM. | Column | Description | | --- | --- | | id | Unique ID for the server | | hostname | The name of the machine | | cpu_number | Number of cores | | total_mem | Total RAM |

host_usage: Stores dynamic data collected every minute. | Column | Description | | --- | --- | | timestamp | When the data was collected | | host_id | Links back to the host_info table | | memory_free | Free RAM in MB | | cpu_idle | % of CPU doing nothing |

## Testing

Bash Testing: Verified that lscpu and vmstat were being parsed correctly.

Database Check: Confirmed data was saved using SELECT * FROM host_usage.

## Automation

Verified crontab was adding a new row every 60 seconds.

## Future Improvements

Handle Hardware Updates: Update logic to handle changes in existing hostnames.

