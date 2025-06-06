.PHONY: build

run-dist:
	./build/install/app/bin/app

run:
	make run-dist

run-help:
	./build/install/app/bin/app -h

checkstyle:
	./gradlew checkstyleMain checkstyleTest

build: test checkstyle
	./gradlew clean
	./gradlew installDist

test: # Run tests
	./gradlew test

lint: # Check code style
	./gradlew checkstyleMain checkstyleTest

test-report: # Check code coverage
	./gradlew jacocoTestReport