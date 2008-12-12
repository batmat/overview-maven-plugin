def file = new File(basedir, 'target/site/images/overview.png')

log.debug("Checking if $file exists.")
assert file.exists()
log.debug("$file exists.")

file = new File(basedir, 'target/site/overview.html')

log.debug("Checking if $file exists.")
assert file.exists()
log.debug("$file exists.")
