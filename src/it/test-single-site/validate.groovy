def file = new File(basedir, 'target/site/images/overview.png')
assert file.exists()
file = new File(basedir, 'target/site/overview.html')
assert file.exists()