# havent yet decided if this makefile should be commited and maintained

.PHONY: help

help:
	grep -E '^[a-z]+:' Makefile # display all targets

clean:
	./gradlew clean

containers:
	docker compose up -d

oas:
	./gradlew openApiGenerate

run:
	./gradlew :app:bootRun

psql:
	docker exec -it spring-database psql -U postgres -d mydb
