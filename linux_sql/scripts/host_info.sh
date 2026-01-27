#!/bin/bash

# 1. Setup Arguments from pseudocode
psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

# Check args count
if [ "$#" -ne 5 ]; then
    echo "Wrong number of parameters"
    echo "Usage: ./scripts/host_info.sh psql_host psql_port db_name psql_user psql_password"
    exit 1
fi

# 2. Parse Hardware Specifications
lscpu_out=`lscpu`
hostname=$(hostname -f)

# Get CPU Number
cpu_number=$(echo "$lscpu_out"  | egrep "^CPU\(s\):" | awk '{print $2}' | xargs)

# Get CPU Architecture
cpu_architecture=$(echo "$lscpu_out" | egrep "^Architecture:" | awk '{print $2}' | xargs)

# Get CPU Model
cpu_model=$(echo "$lscpu_out" | egrep "^Model name:" | awk -F: '{print $2}' | xargs)

# Get CPU MHz (Standard check)
cpu_mhz=$(echo "$lscpu_out" | grep "CPU MHz" | awk '{print $3}' | xargs)

# FIX: If MHz is empty, calculate it from Model Name (e.g., "2.20GHz" -> 2200)
if [ -z "$cpu_mhz" ]; then
    cpu_mhz=$(echo "$lscpu_out" | grep "Model name" | awk '{print $NF}' | sed 's/GHz//' | awk '{print $1 * 1000}')
fi

# Get L2 Cache (remove 'K' suffix)
l2_cache=$(echo "$lscpu_out" | egrep "^L2 cache:" | awk '{print $3}' | sed 's/K//' | xargs)

# Get Total Memory
total_mem=$(grep MemTotal /proc/meminfo | awk '{print $2}' | xargs)

# Get Timestamp
timestamp=$(date "+%Y-%m-%d %H:%M:%S")

# 3. Construct Insert Statement
insert_stmt="INSERT INTO host_info(hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, l2_cache, total_mem, timestamp) VALUES('$hostname', $cpu_number, '$cpu_architecture', '$cpu_model', $cpu_mhz, $l2_cache, $total_mem, '$timestamp');"

# 4. Execute Command
export PGPASSWORD=$psql_password
psql -h $psql_host -p $psql_port -d $db_name -U $psql_user -c "$insert_stmt"
exit $?
