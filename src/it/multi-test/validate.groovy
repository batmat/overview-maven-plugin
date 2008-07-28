def file = new File('target/site/images/overview.png')
assert file.exists()
assert file.size() > 23000
