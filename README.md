# Transactional Key Value Store

A interactive command line application project to a transactional key value store.

The user can enter commands to set/get/delete key/value pairs and count values. 
All values are treated as strings. The key/value data is only stored in memory for this application.

This application allow the user to perform operations in transactions, which allows the user to commit
or roll back the changes to the key values store. It includes the ability to nest transactions and roll 
back and commit within nested transactions.
