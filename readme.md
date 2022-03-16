# Instructions:

## For property file
- Change the necessary fields in the property file.
`src/main/files/dataFile.properties`

- Update the path of the property file from the BASE class.
`src/test/java/com/zopsmart/meet/MeetBase.java`
`PATH_PROPERTY_FILE`

## SQL Table Structure
```
  CREATE TABLE Meet (
        MeetCode VARCHAR(14) NOT NULL PRIMARY KEY,
        MeetStartDate VARCHAR(12) NOT NULL,
        MeetStartTime VARCHAR(8) NOT NULL,
        MeetEndDate VARCHAR(12) NOT NULL,
        MeetEndTime VARCHAR(8) NOT NULL,
        Status VARCHAR(30)
  );
```

## Supported database:
- mysql
- postgresql
