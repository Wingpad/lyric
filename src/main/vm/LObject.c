#include "LObject.h"

// For strlen
#include <string.h>
// For malloc
#include <stdlib.h>
// For assert
#include <assert.h>
// For vargs
#include <stdarg.h>

/**
 * Simple Bob Jenkins's hash algorithm taken from the
 * wikipedia description.
 */
static uint32_t default_hash(char *key)
{
    size_t len = strlen(key);
    uint32_t hash = 0;
    uint32_t i = 0;

    for(hash = i = 0; i < len; ++i)
    {
        hash += key[i];
        hash += (hash << 10);
        hash ^= (hash >> 6);
    }

    hash += (hash << 3);
    hash ^= (hash >> 11);
    hash += (hash << 15);

    return hash;
}

LObjectNode* LObject_findField(LObject* self, char* name, uint32_t* hash) {
	LObjectNode* curr;

	// If no hash value was provided
	if (*hash == 0) {
		// Initialize it
		*hash = default_hash(name);
	}

	// Then, for each of the nodes
	for (curr = self->first; curr; curr = curr->next) {
		// If a node matches
		if (curr->hash == *hash) {
			// Return it
			return curr;
		}
	}

	// Otherwise, if no match was found and a superclass exists
	if (self->base) {
		// Search it as well
		return LObject_findField(self->base, name, hash);
	} else {
		// Otherwise, just return NULL to indicate a match was not found
		return NULL;
	}
}

void LObject_set(LObject* self, char* name, void* value) {
    uint32_t hash = 0;
    // Try and locate the field using the field finder
    LObjectNode* found = LObject_findField(self, name, &hash);

    // If the node was found
    if (found) {
      // Update its value, otherwise...
      found->value   = value;
    } else {
      // Initialize a new node w/ the hash and value pointing to the next in the sequence
      LObjectNode* newNode  = malloc(sizeof(LObjectNode));
      newNode->next  = self->first;
      newNode->hash  = hash;
      newNode->value = value;
	  newNode->finalized = false;
      // And set it as the first node
      self->first = newNode;
    }
}

void* LObject_get(LObject* self, char* name) {
	uint32_t hash = 0;
	// Try and locate the field using the field finder
	LObjectNode* found = LObject_findField(self, name, &hash);

	// If the node was found
	if (found) {
		// Update its value, otherwise...
		return found->value;
	} else {
		// Otherwise, return NULL
		return NULL;
	}
}

LObjectNode* LObject_findFunction(LObject* self, char* name, char* signature, uint32_t* hash) {
	LObjectNode* curr;
	// If no hash value was provided
	if (*hash == 0) {
		// Initialize it
		*hash = default_hash(name);
	}
	// If no name was provided
	if (name == NULL) {
		// Use invoke
		name = "invoke";
	}
	// Then, for each of the nodes
	for (curr = self->first; curr; curr = curr->next) {
		// If a node matches
		if (curr->hash == *hash) {
			// Grab the value
			struct LFunction* value = (struct LFunction*)curr->value;
			// Check its signature to see if it matches the desired one
			if (strcmp(value->signature, signature) == 0) {
				// And return it if it does
				return curr;
			}
		}
	}
	// Otherwise, if no match was found and a superclass exists
	if (self->base) {
		// Search it as well
		return LObject_findFunction(self->base, name, signature, hash);
	} else {
		// Otherwise, just return NULL to indicate a match was not found
		return NULL;
	}
}

struct LFunction* LObject_lift(LObject* self, char* name, char* signature) {
	uint32_t hash = 0;
	LObjectNode* node = (struct LFunction*) LObject_findFunction(self, name, signature, &hash);
	return node == NULL ? NULL : node->value;
}

void LObject_put(LObject* self, char* name, LFunction* value) {
	uint32_t hash = 0;
	// Try to find the function
	LObjectNode* node = LObject_findFunction(self, name, value->signature, &hash);
	// And update the self
	value->self = self;
	// If it was found
	if (node) {
		// set the value of the node to the function
		node->value = value;
	} else {
		// Initialize a new node w/ the hash and value pointing to the next in the sequence
		LObjectNode* newNode = malloc(sizeof(LObjectNode));
		newNode->next = self->first;
		newNode->hash = hash;
		newNode->value = value;
		newNode->finalized = true;
		// And set it as the first node
		self->first = newNode;
	}
}