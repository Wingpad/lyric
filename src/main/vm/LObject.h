#ifndef LYRIC_OBJECT_H
#define LYRIC_OBJECT_H

// For uint32_t
#include <stdint.h>
// For bool
#include <stdbool.h>
// For LFunction
#include "LFunction.h"

// TODO Make non-recursive versions of set and findField
// TODO Add type validation to set/get, how??

// Forward Declare LFunction
struct LFunction;

typedef struct LObjectNode {
	void* value;
	uint32_t hash;
	// TODO Make finalizing a field actually do something (and a method to "freeze" a field)
	bool finalized;
	struct LObjectNode* next;
} LObjectNode;

typedef struct LObject {
	LObjectNode* first;
	struct LObject* base;
	char* type;
} LObject;

typedef struct LClass {
	struct LObject obj;
	LObjectNode* objsNodes;
} LClass;

LObjectNode*		LObject_findField(LObject* self, char* name, uint32_t* hash);
void				LObject_set(LObject* self, char* name, void* value);
void				LObject_put(LObject* self, char* name, struct LFunction* value);
void*				LObject_get(LObject* self, char* name);
struct LFunction*	LObject_lift(LObject* self, char* name, char* signature);

#endif
