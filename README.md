ntp
===

Simple NTP Service (One Producer - Multiple Consumers)

Running
=======
```bash
git clone git@github.com:koadr/ntp.git
cd ntp
./sbt run # Takes an optional integer argument for the number of consumers you want to run. Defaults to 15 consumers.
```

Testing
=======
```bash
./sbt test
```
