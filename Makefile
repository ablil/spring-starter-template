# havent yet decided if this makefile should be commited and maintained

.PHONY: assemble

profiles ?= default

clean:
	./gradlew clean

build:
	./gradlew build

containers:
	docker compose up -d

oas:
	./gradlew application:openApiGenerate

run:
	./gradlew :application:bootRun -Pprofiles=$(profiles)
