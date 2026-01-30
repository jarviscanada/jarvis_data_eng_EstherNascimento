#!/bin/bash

# Capture CLI arguments
cmd=$1
db_username=$2
db_password=$3

# Start docker if it is not running

sudo systemctl status docker > /dev/null || sudo systemctl start docker

# Check container status (exists or not)
docker container inspect jrvs-psql > /dev/null 2>&1
container_status=$?

# Switch case to handle create|stop|start options
case $cmd in 
  create)
  
    
    if [ $container_status -eq 0 ]; then
      echo "Error: Container 'jrvs-psql' already exists."
      exit 1	
    fi

    if [ -z "$db_username" ] || [ -z "$db_password" ]; then
      echo "Error: Create requires username and password."
      echo "Usage: ./scripts/psql_docker.sh create [db_username] [db_password]"
      exit 1
    fi
    
    # 3. Create volume and container
    echo "Creating pgdata volume..."
    docker volume create pgdata
    
    echo "Creating jrvs-psql container..."
    docker run --name jrvs-psql -e POSTGRES_PASSWORD=$db_password -d -v pgdata:/var/lib/postgresql/data -p 5432:5432 postgres:9.6-alpine
    
    # Check if the command was successful
    if [ $? -eq 0 ]; then
        echo "Container created successfully."
        exit 0
    else
        echo "Error: Failed to create container."
        exit 1
    fi
    ;;

  start)
    # Check if container has been created
    if [ $container_status -ne 0 ]; then
        echo "Error: Container 'jrvs-psql' has not been created yet."
        exit 1
    fi

    # Start the container
    docker container start jrvs-psql
    exit $?
    ;;
  
  stop)
    # Check if container has been created
    if [ $container_status -ne 0 ]; then
        echo "Error: Container 'jrvs-psql' has not been created yet."
        exit 1
    fi

    # Stop the container
    docker container stop jrvs-psql
    exit $?
    ;;
  
  *)
    echo "Error: Invalid command."
    echo "Usage: $0 start|stop|create [db_username] [db_password]"
    exit 1
    ;;
esac
