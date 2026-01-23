#!/bin/bash

# 1. Setup Arguments
psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

# Check argument count
if [ "$#" -ne 5 ]; then
    echo "Wrong number of parameters"
    echo "Usage: ./scripts/host_usage.sh psql_host psql_port db_name psql_user psql_password"
    exit 1
fi

# 2. Capture Machine Statistics
hostname=$(hostname -f)
vmstat_mb=$(vmstat --unit M)

# 3. Parse Data
# Memory Free (in MB) - Column 4 of vmstat
memory_free=$(echo "$vmstat_mb" | awk '{print $4}' | tail -n1 | xargs)

# CPU Idle 
cpu_idle=$(echo "$vmstat_mb" | awk '{print $15}' | tail -n1 | xargs)

# CPU Kernel 
cpu_kernel=$(echo "$vmstat_mb" | awk '{print $14}' | tail -n1 | xargs)

# Disk IO 
disk_io=$(vmstat -d | awk '{print $10}' | tail -n1 | xargs)

# Disk Available 
disk_available=$(df -BM / | awk '{print $4}' | tail -n1 | sed 's/M//' | xargs)

# Timestamp - Format: YYYY-MM-DD HH:MM:SS
timestamp=$(date "+%Y-%m-%d %H:%M:%S")

# 4. Construct Query
# Insert Query using subquery
insert_stmt="INSERT INTO host_usage(timestamp, host_id, memory_free, cpu_idle, cpu_kernel, disk_io, disk_available) VALUES('$timestamp', (SELECT id FROM host_info WHERE hostname='$hostname'), $memory_free, $cpu_idle, $cpu_kernel, $disk_io, $disk_available);"

# 5. Execute
export PGPASSWORD=$psql_password
psql -h $psql_host -p $psql_port -d $db_name -U $psql_user -c "$insert_stmt"
exit $?
