def file1 = new File(basedir, 'target/site/images/overview.png')
file1.deleteOnExit()
def file2 = new File(basedir, 'target/site/overview.html')
file2.deleteOnExit()
