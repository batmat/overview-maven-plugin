def file = new File('target/site/images/overview.png')

log.debug("Checking if $file exists.")
assert file.exists()
log.debug("$file exists.")

log.debug("Checking if $file has expected size.")
assert file.size() > 23000
log.debug("$file has expected size.")
