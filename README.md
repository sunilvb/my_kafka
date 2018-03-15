echo "# my_kafka" >> README.md
git init
git add README.md
git add sample-spring-kafka-consumer/*.*
git commit -m "first commit"
git remote add origin https://github.com/sunilvb/my_kafka.git
git push -u origin master

git add --all
git commit -m "5th commit"
git push -u origin master