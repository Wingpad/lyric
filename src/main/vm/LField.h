#ifndef LYRIC_FIELD_H
#define LYRIC_FIELD_H

typedef struct LField {
  char* type;
  char* name;
  void* defaultValue;
} LField;

#endif
