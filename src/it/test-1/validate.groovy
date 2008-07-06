
def file = new File(basedir, 'target/site/images/overview.png')
log.warn("File is ${file.absoluteFile}")
assert file.exists()
