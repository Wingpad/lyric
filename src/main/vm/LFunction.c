#include "LFunction.h"

// For strlen
#include <string.h>
// For malloc
#include <stdlib.h>
// For assert
#include <assert.h>
// For vargs
#include <stdarg.h>

LObject* LFunction_invoke(LFunction* self, ...) {
	va_list args1, args2;
	uint32_t i, argc = 0;
	// Grab the function
	NFunction function = self->native;
	// Assert that it is not null
	assert(function);
	// Start the va_list
	va_start(args1, self);
	// Copy it
	va_copy(args2, args1);
	// Increment the arguments count for each of the arguments
	while (va_arg(args1, void*)) argc++;
	// Then end the first list
	va_end(args1);
	// If there are actually arguments
	if (argc) {
		// Allocate a new vector
		void** argv = malloc(argc * sizeof(void*));
		// And copy the arguments
		for (i = 0; i < argc; i++) {
			argv[i] = va_arg(args2, void*);
		}
		// End the varargs
		va_end(args2);
		// Then call the function
		return function(self->self, argc, argv);
	}
	else {
		// Otherwise, end the varargs
		va_end(args2);
		// Then call the function
		return function(self->self, 0, NULL);
	}
}

LFunction* LFunction_clone(LFunction* function, LObject* newSelf) {
	// Initialize a function with the correct fields
	return LFunction_init(newSelf, function->native, function->signature);
}

LFunction* LFunction_init(LObject* self, NFunction function, char* signature) {
	// Allocate a new function
	LFunction* newFunction = (LFunction*)malloc(sizeof(LFunction));
	// Copy all of the properties to it
	newFunction->self = self;
	newFunction->native = function;
	newFunction->signature = signature;
	// Then, return it
	return newFunction;
}