# Jenkins AWS Kinesis Logs Plugin

A Jenkins plugin for forwarding your build logs to a AWS Kinesis data stream.

The idea with this plugin is that you should be able to send your logs to
AWS Kinesis, so that you can apply things like Kinesis Firehose for log 
archiving, Kinesis Analytics for ?? etc.

## Disclaimer

* I do not know Java. I'm not a Java developer. This is the first time ever I
  write Java.
* It was written with the intention for me to learn a little bit of Java,
  and understand Jenkins plugin development a little bit as well.
* This is a very rough draft, with just basic functionality.
