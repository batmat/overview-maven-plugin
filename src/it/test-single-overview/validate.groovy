def file = new File(basedir, 'target/site/images/overview.png')

log.debug("Checking if $file exists.")
assert file.exists()
log.debug("$file exists.")

log.debug("Checking if $file has expected size.")
assert file.size() > 3500
log.debug("$file has expected size.")
