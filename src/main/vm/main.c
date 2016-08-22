#include "LFunction.h"
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>

LObject* Int;
LObject* String;
LFunction* logWithString;

LObject* Int_initWithI(LObject* self, uint32_t argc, void** argv) {
    int32_t* native = (int32_t*)argv[0];
    self = (LObject*)LClass_instantiate(Int);
    LObject_set(self, "value", native);
    return self;
};

LObject* Int_plusWithInt(LObject* self, uint32_t argc, void** argv) {
    LObject* other = (LObject*)argv[0];
    int32_t* ours = LObject_get(self, "value");
    int32_t* theirs = LObject_get(other, "value");
    int32_t* __val0 = (int32_t*)malloc(sizeof(int32_t));
    *__val0 = *ours + *theirs;
    return LFunction_invoke(LObject_lift(Int, NULL, "I"), __val0, NULL);
};

LObject* Int_toString(LObject* self, uint32_t argc, void** argv) {
    int32_t* ours = LObject_get(self, "value");
    char* buffer = (char*)calloc(33, sizeof(char));
    sprintf_s(buffer, 33, "%d", *ours);
    return LFunction_invoke(LObject_lift(String, NULL, "C"), buffer, NULL);
};

LObject* Int_allocate() {
    LObject* lClass = LObject_new("LClass", NULL);
    lClass->first = LObjectNode_new("LFunction", "invoke", LFunction_new(lClass, Int_initWithI, "I"), NULL);
    lClass->first = LObjectNode_new("C", "name", "Int", lClass->first);
    lClass->metadata = LObjectNode_new("I", "value", NULL, NULL);
    lClass->metadata = LObjectNode_new("LFunction", "plus", LFunction_new(NULL, Int_plusWithInt, "Int"), (LObjectNode*)lClass->metadata);
    lClass->metadata = LObjectNode_new("LFunction", "toString", LFunction_new(NULL, Int_toString, ""), (LObjectNode*)lClass->metadata);
    return lClass;
};

LObject* String_initWithC(LObject* self, uint32_t argc, void** argv) {
    char* native = (char*)argv[0];
    self = (LObject*)LClass_instantiate(String);
    LObject_set(self, "value", native);
    return self;
};

LObject* String_allocate() {
    LObject* lClass = LObject_new("LClass", NULL);
    lClass->first = LObjectNode_new("LFunction", "invoke", LFunction_new(lClass, String_initWithC, "C"), NULL);
    lClass->first = LObjectNode_new("C", "name", "String", lClass->first);
    lClass->metadata = LObjectNode_new("C", "value", NULL, NULL);
    return lClass;
};

LObject* Lyric_logWithString(LObject* self, uint32_t argc, void** argv) {
    LObject* s = (LObject*)argv[0];
    char* buffer = LObject_get(s, "value");
    printf("%s\n", buffer);
};

LObject* Lyric_main(LObject* self, uint32_t argc, void** argv) {
    int32_t* __val0 = (int32_t*)malloc(sizeof(int32_t));
    *__val0 = 4;
    LObject* i = LFunction_invoke(LObject_lift(Int, NULL, "I"), __val0, NULL);
    int32_t* __val1 = (int32_t*)malloc(sizeof(int32_t));
    *__val1 = 4;
    LObject* j = LFunction_invoke(LObject_lift(Int, NULL, "I"), __val1, NULL);
    LObject* k = LFunction_invoke(LObject_lift(i, "plus", "Int"), j, NULL);
    LFunction_invoke(logWithString, LFunction_invoke(LObject_lift(k, "toString", ""), NULL), NULL);
};

void setupGlobals() {
    logWithString = LFunction_new(NULL, Lyric_logWithString, "String");
    Int = Int_allocate();
    String = String_allocate();
}

int main(void) {
    setupGlobals();

    Lyric_main(NULL, 0, NULL);

	system("PAUSE");
}