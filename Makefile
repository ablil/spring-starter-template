# havent yet decided if this makefile should be commited and maintained

.PHONY: assemble

assemble:
	./gradlew app:assemble -x app:jibDockerBuild

help:
	grep -E '^[a-z]+:' Makefile # display all targets

clean:
	./gradlew clean

build:
	./gradlew build

containers:
	docker compose up -d

oas:
	./gradlew openApiGenerate

run:
	./gradlew :app:bootRun

psql:
	docker exec -it spring-database psql -U postgres -d mydb
