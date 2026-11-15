# A4 – What Should Be Serialized?

## A4.1 – Decide What to Persist

The Hostel object contains a list of Room objects. Each Room contains a list of Resident objects.

### Fields to serialize

| Class | Field | Decision | Reason |
|---|---|---|---|
| Hostel | hostelName | Serialize | Identifies the hostel |
| Hostel | rooms | Serialize | Stores all room objects |
| Room | roomNumber | Serialize | Identifies each room |
| Room | residents | Serialize | Stores the residents in each room |
| Resident | name | Serialize | Required resident information |
| Resident | registerNumber | Serialize | Identifies the student |
| Resident | phoneNumber | Serialize | Required contact information |
| Resident | password | transient | A password must not be saved as plain text |
| Resident | databaseConnection | transient | A live database connection is a runtime resource and cannot be restored as a normal saved object |

The password should not be stored in plain text. If authentication is required, a securely salted password hash should be stored separately. The database connection should be created again after loading, using the application's database configuration or connection pool.

### Object graph serialization

When the Hostel object is serialized, Java follows its references and saves the reachable serializable object graph.

The object graph is:

Hostel
  -> List of Room objects
      -> Each Room
          -> List of Resident objects
              -> Resident fields

Hostel, Room and Resident implement Serializable. The lists are also serializable collections. Java serialization saves the non-transient instance fields of these objects. It tracks object references, so shared references are handled without repeatedly writing the same object as a new independent object.

The transient password and databaseConnection fields are not written to the stream. After deserialization, these fields have their default values. For these reference fields, the default value is null.

If a non-transient field refers to a non-serializable object, serialization can fail with NotSerializableException.

## A4.2 – Plan for Change

A new phoneNumber field is added to Resident after older serialized files have already been created.

If the class keeps the same explicit serialVersionUID and the old file is compatible with the new class, the old file can be loaded. The phoneNumber field was not present in the old stream, so Java assigns its default value, null.

The application should handle this missing value after loading, for example by displaying "Not provided".

serialVersionUID is a version identifier for a serializable class. During deserialization, Java compares the identifier stored in the file with the identifier of the current class.

- Matching serialVersionUID: compatible changes, such as adding a field, can be supported. A missing new field receives its default value.
- Different serialVersionUID: Java normally throws InvalidClassException because the class versions are incompatible.

An explicit serialVersionUID helps control compatibility. It does not automatically migrate or validate old data. Each serializable class in the object graph should have a deliberately managed identifier.

### Practical versioning test

The versioning test saved a VersionedResident object using the old class, which contained name and registerNumber. The new class added phoneNumber and retained serialVersionUID = 1L.

The old file loaded successfully using the new class. The name and register number were restored, while phoneNumber was null because the old file did not contain that field.

## Alternative: CSV or JSON

The hostel data could also be stored as CSV or JSON instead of Java object serialization.

| Comparison | Java serialization | CSV | JSON |
|---|---|---|---|
| Format | Binary object stream | Delimited text | Structured text |
| Readability | Not directly human-readable | Human-readable | Human-readable |
| File size | Includes object and class metadata; size depends on the graph | Often compact for simple tabular data | Often has more structural text |
| Saving n residents | O(n) for traversing the object graph | O(n) for writing records | O(n) for writing objects |
| Loading n residents | O(n) for restoring the graph | O(n) for parsing records | O(n) for parsing objects |
| Adding a field | Requires compatible class versioning and a default for missing fields | Add a column and handle missing values | Add a named property and handle missing keys |

The actual file size and execution time depend on the data, encoding, libraries and implementation. No format is always the smallest or fastest.

CSV is suitable for simple tabular records. A CSV implementation must correctly handle delimiters, quotes and line breaks inside values. JSON is suitable for nested data such as a hostel containing rooms and residents. JSON libraries can map named properties to Java objects.

For CSV and JSON, a newly added phone number can be represented by a new column or property. Older records without that value should be assigned a default such as null or "Not provided".

Passwords should not be exported as plain text, and live database connections should not be written into CSV or JSON.

## Conclusion

The Hostel object graph can be serialized while excluding sensitive passwords and runtime database connections with transient. Explicit serialVersionUID supports controlled compatibility when fields are added. CSV and JSON provide readable alternatives, with their own parsing and schema-handling requirements.
