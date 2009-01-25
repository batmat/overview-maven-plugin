def file = new File(basedir, 'target/site/images/overview.png')

log.debug("Checking if $file exists.")
assert file.exists() : "$file doesn't exist"
log.debug("$file exists.")

log.debug("Checking if $file has expected size.")
assert file.size() > 20000
log.debug("$file has expected size.")
