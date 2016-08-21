#ifndef LYRIC_FUNCTION_H
#define LYRIC_FUNCTION_H

#include <stdint.h>
#include "LObject.h"

struct LObject;

typedef struct LObject* (*NFunction)(struct LObject* self, uint32_t argc, void** argv);

typedef struct LFunction {
	struct LObject* self;
	NFunction native;
	char* signature;
} LFunction;

struct LObject*	LFunction_invoke(LFunction* self, ...);
LFunction*		LFunction_clone(LFunction* function, struct LObject* newSelf);
LFunction*		LFunction_init(struct LObject* self, NFunction function, char* signature);

#endif