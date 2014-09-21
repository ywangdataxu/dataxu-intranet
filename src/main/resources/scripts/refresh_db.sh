export PGPASSWORD=dxintranet

FILES="/Users/ywang/git/dataxu-intranet/src/main/resources/db-scripts/*.sql"
for f in $FILES
do
    echo "Processing $f"
    `psql -Udxintranet dxintranet -a -f $f`
done