#!/usr/bin/env bash

current_date=$(gdate -R -d "100 days ago")

for i in {1..100}
do
  echo "Dummy commit $i" > dummy_file.txt

  git add dummy_file.txt

  GIT_COMMITTER_DATE="$current_date" git commit -m "Dummy commit $i" --date="$current_date"

  current_date=$(date -R -d "$current_date + 1 day")
done

rm dummy_file.txt
