#!/bin/bash

export $(echo $(cat .env | sed 's/#.*//g' | sed 's/\r//g' | xargs) | envsubst)

ssh "$REMOTE_USER"@"$REMOTE_HOST" "sudo /etc/init.d/mariadb start"
ssh "$REMOTE_USER"@"$REMOTE_HOST" "cd $REMOTE_PATH && sed -i s/__user__/$MARIADB_USER/g bin/remote/setup_hootsDB.sql && sed -i s/__password__/$MARIADB_PWD/g bin/remote/setup_hootsDB.sql"
ssh "$REMOTE_USER"@"$REMOTE_HOST" "cd $REMOTE_PATH && sudo -u mysql mariadb < bin/remote/setup_hootsDB.sql && sudo -u mysql mariadb -D hootsDB < db/recreate_tables.sql"
