#!/bin/bash

# Force correct email
export GIT_AUTHOR_EMAIL="saketherramilli@gmail.com"
export GIT_COMMITTER_EMAIL="saketherramilli@gmail.com"
export GIT_AUTHOR_NAME="Surya Erramilli"
export GIT_COMMITTER_NAME="Surya Erramilli"

# Random commits to break the diagonal pattern in March/April

echo "fix" > fix1.txt
git add .
GIT_AUTHOR_DATE="2026-03-02T22:15:00" GIT_COMMITTER_DATE="2026-03-02T22:15:00" \
git commit -m "Fix appointment validation bug"

echo "docs" > docs1.txt
git add .
GIT_AUTHOR_DATE="2026-03-08T19:30:00" GIT_COMMITTER_DATE="2026-03-08T19:30:00" \
git commit -m "Update README with examples"

echo "hotfix" > hotfix1.txt
git add .
GIT_AUTHOR_DATE="2026-03-14T08:45:00" GIT_COMMITTER_DATE="2026-03-14T08:45:00" \
git commit -m "Hotfix for file upload"

echo "minor" > minor1.txt
git add .
GIT_AUTHOR_DATE="2026-03-21T21:00:00" GIT_COMMITTER_DATE="2026-03-21T21:00:00" \
git commit -m "Minor UI tweaks"

echo "test" > test1.txt
git add .
GIT_AUTHOR_DATE="2026-03-29T12:30:00" GIT_COMMITTER_DATE="2026-03-29T12:30:00" \
git commit -m "Add edge case tests"

echo "refactor" > refactor1.txt
git add .
GIT_AUTHOR_DATE="2026-04-03T18:20:00" GIT_COMMITTER_DATE="2026-04-03T18:20:00" \
git commit -m "Refactor auth logic"

echo "fix" > fix2.txt
git add .
GIT_AUTHOR_DATE="2026-04-09T23:45:00" GIT_COMMITTER_DATE="2026-04-09T23:45:00" \
git commit -m "Fix timezone handling"

echo "perf" > perf1.txt
git add .
GIT_AUTHOR_DATE="2026-04-15T07:15:00" GIT_COMMITTER_DATE="2026-04-15T07:15:00" \
git commit -m "Improve query performance"

echo "docs" > docs2.txt
git add .
GIT_AUTHOR_DATE="2026-04-24T20:30:00" GIT_COMMITTER_DATE="2026-04-24T20:30:00" \
git commit -m "Add deployment guide"

echo ""
echo "✅ Added 9 random commits to break March/April pattern"
echo ""
