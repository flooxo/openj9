/*******************************************************************************
 * Copyright IBM Corp. and others 2023
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which accompanies this
 * distribution and is available at https://www.eclipse.org/legal/epl-2.0/
 * or the Apache License, Version 2.0 which accompanies this distribution and
 * is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * This Source Code may also be made available under the following
 * Secondary Licenses when the conditions for such availability set
 * forth in the Eclipse Public License, v. 2.0 are satisfied: GNU
 * General Public License, version 2 with the GNU Classpath
 * Exception [1] and GNU General Public License, version 2 with the
 * OpenJDK Assembly Exception [2].
 *
 * [1] https://www.gnu.org/software/classpath/license.html
 * [2] https://openjdk.org/legal/assembly-exception.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0 OR GPL-2.0-only WITH Classpath-exception-2.0 OR GPL-2.0-only WITH OpenJDK-assembly-exception-1.0
 *******************************************************************************/
package org.openj9.test.jep442.upcall;

import org.testng.Assert;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import static java.lang.invoke.MethodType.methodType;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.SequenceLayout;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;

import static java.lang.foreign.Linker.*;
import static java.lang.foreign.ValueLayout.*;

/**
 * The helper class that contains all upcall method handles with primitive types or struct
 * as arguments.
 */
public class UpcallMethodHandles {
	private static final Lookup lookup = MethodHandles.lookup();
	private static Arena arena = Arena.ofAuto();
	private static boolean isAixOS = System.getProperty("os.name").toLowerCase().contains("aix");

	static final MethodType MT_Bool_Bool_MemSegmt = methodType(boolean.class, boolean.class, MemorySegment.class);
	static final MethodType MT_Segmt_Bool_MemSegmt = methodType(MemorySegment.class, boolean.class, MemorySegment.class);
	static final MethodType MT_Char_Char_MemSegmt = methodType(char.class, char.class, MemorySegment.class);
	static final MethodType MT_Segmt_MemSegmt_Char = methodType(MemorySegment.class, MemorySegment.class, char.class);
	static final MethodType MT_Byte_Byte_MemSegmt = methodType(byte.class, byte.class, MemorySegment.class);
	static final MethodType MT_Segmt_Byte_MemSegmt = methodType(MemorySegment.class, byte.class, MemorySegment.class);
	static final MethodType MT_Short_Short_MemSegmt = methodType(short.class, short.class, MemorySegment.class);
	static final MethodType MT_Segmt_MemSegmt_Short = methodType(MemorySegment.class, MemorySegment.class, short.class);
	static final MethodType MT_Int_Int_MemSegmt = methodType(int.class, int.class, MemorySegment.class);
	static final MethodType MT_Segmt_Int_MemSegmt = methodType(MemorySegment.class, int.class, MemorySegment.class);
	static final MethodType MT_Long_Long_MemSegmt = methodType(long.class, long.class, MemorySegment.class);
	static final MethodType MT_Segmt_MemSegmt_Long = methodType(MemorySegment.class, MemorySegment.class, long.class);
	static final MethodType MT_Long_Int_MemSegmt = methodType(long.class, int.class, MemorySegment.class);
	static final MethodType MT_Float_Float_MemSegmt = methodType(float.class, float.class, MemorySegment.class);
	static final MethodType MT_Segmt_Float_MemSegmt = methodType(MemorySegment.class, float.class, MemorySegment.class);
	static final MethodType MT_Double_Double_MemSegmt = methodType(double.class, double.class, MemorySegment.class);
	static final MethodType MT_Segmt_MemSegmt_Double = methodType(MemorySegment.class, MemorySegment.class, double.class);
	static final MethodType MT_Segmt_MemSegmt_MemSegmt = methodType(MemorySegment.class, MemorySegment.class, MemorySegment.class);
	static final MethodType MT_MemSegmt_MemSegmt_MemSegmt = methodType(MemorySegment.class, MemorySegment.class, MemorySegment.class);
	static final MethodType MT_MemSegmt = methodType(MemorySegment.class);

	public static final MethodHandle MH_add2BoolsWithOr;
	public static final MethodHandle MH_addBoolAndBoolFromPointerWithOr;
	public static final MethodHandle MH_addBoolAndBoolFromPtrWithOr_RetPtr;
	public static final MethodHandle MH_addBoolAndBoolFromPtrWithOr_RetArgPtr;
	public static final MethodHandle MH_createNewCharFrom2Chars;
	public static final MethodHandle MH_createNewCharFromCharAndCharFromPointer;
	public static final MethodHandle MH_createNewCharFromCharAndCharFromPtr_RetPtr;
	public static final MethodHandle MH_createNewCharFromCharAndCharFromPtr_RetArgPtr;
	public static final MethodHandle MH_add2Bytes;
	public static final MethodHandle MH_addByteAndByteFromPointer;
	public static final MethodHandle MH_addByteAndByteFromPtr_RetPtr;
	public static final MethodHandle MH_addByteAndByteFromPtr_RetArgPtr;
	public static final MethodHandle MH_add2Shorts;
	public static final MethodHandle MH_addShortAndShortFromPointer;
	public static final MethodHandle MH_addShortAndShortFromPtr_RetPtr;
	public static final MethodHandle MH_addShortAndShortFromPtr_RetArgPtr;
	public static final MethodHandle MH_add2Ints;
	public static final MethodHandle MH_addIntAndIntFromPointer;
	public static final MethodHandle MH_addIntAndIntFromPtr_RetPtr;
	public static final MethodHandle MH_addIntAndIntFromPtr_RetArgPtr;
	public static final MethodHandle MH_add3Ints;
	public static final MethodHandle MH_addIntAndChar;
	public static final MethodHandle MH_add2IntsReturnVoid;
	public static final MethodHandle MH_add2Longs;
	public static final MethodHandle MH_addLongAndLongFromPointer;
	public static final MethodHandle MH_addLongAndLongFromPtr_RetPtr;
	public static final MethodHandle MH_addLongAndLongFromPtr_RetArgPtr;
	public static final MethodHandle MH_add2Floats;
	public static final MethodHandle MH_addFloatAndFloatFromPointer;
	public static final MethodHandle MH_addFloatAndFloatFromPtr_RetPtr;
	public static final MethodHandle MH_addFloatAndFloatFromPtr_RetArgPtr;
	public static final MethodHandle MH_add2Doubles;
	public static final MethodHandle MH_addDoubleAndDoubleFromPointer;
	public static final MethodHandle MH_addDoubleAndDoubleFromPtr_RetPtr;
	public static final MethodHandle MH_addDoubleAndDoubleFromPtr_RetArgPtr;
	public static final MethodHandle MH_compare;

	public static final MethodHandle MH_addBoolAndBoolsFromStructWithXor;
	public static final MethodHandle MH_addBoolAnd20BoolsFromStructWithXor;
	public static final MethodHandle MH_addBoolFromPointerAndBoolsFromStructWithXor;
	public static final MethodHandle MH_addBoolFromPointerAndBoolsFromStructWithXor_returnBoolPointer;
	public static final MethodHandle MH_addBoolAndBoolsFromStructPointerWithXor;
	public static final MethodHandle MH_addBoolAndBoolsFromNestedStructWithXor;
	public static final MethodHandle MH_addBoolAndBoolsFromNestedStructWithXor_reverseOrder;
	public static final MethodHandle MH_addBoolAndBoolsFromStructWithNestedBoolArray;
	public static final MethodHandle MH_addBoolAndBoolsFromStructWithNestedBoolArray_reverseOrder;
	public static final MethodHandle MH_addBoolAndBoolsFromStructWithNestedStructArray;
	public static final MethodHandle MH_addBoolAndBoolsFromStructWithNestedStructArray_reverseOrder;
	public static final MethodHandle MH_add2BoolStructsWithXor_returnStruct;
	public static final MethodHandle MH_add2BoolStructsWithXor_returnStructPointer;
	public static final MethodHandle MH_add3BoolStructsWithXor_returnStruct;

	public static final MethodHandle MH_addByteAndBytesFromStruct;
	public static final MethodHandle MH_addByteAnd20BytesFromStruct;
	public static final MethodHandle MH_addByteFromPointerAndBytesFromStruct;
	public static final MethodHandle MH_addByteFromPointerAndBytesFromStruct_returnBytePointer;
	public static final MethodHandle MH_addByteAndBytesFromStructPointer;
	public static final MethodHandle MH_addByteAndBytesFromNestedStruct;
	public static final MethodHandle MH_addByteAndBytesFromNestedStruct_reverseOrder;
	public static final MethodHandle MH_addByteAndBytesFromStructWithNestedByteArray;
	public static final MethodHandle MH_addByteAndBytesFromStructWithNestedByteArray_reverseOrder;
	public static final MethodHandle MH_addByteAndBytesFromStructWithNestedStructArray;
	public static final MethodHandle MH_addByteAndBytesFromStructWithNestedStructArray_reverseOrder;
	public static final MethodHandle MH_add1ByteStructs_returnStruct;
	public static final MethodHandle MH_add2ByteStructs_returnStruct;
	public static final MethodHandle MH_add2ByteStructs_returnStructPointer;
	public static final MethodHandle MH_add3ByteStructs_returnStruct;

	public static final MethodHandle MH_addCharAndCharsFromStruct;
	public static final MethodHandle MH_addCharAnd10CharsFromStruct;
	public static final MethodHandle MH_addCharFromPointerAndCharsFromStruct;
	public static final MethodHandle MH_addCharFromPointerAndCharsFromStruct_returnCharPointer;
	public static final MethodHandle MH_addCharAndCharsFromStructPointer;
	public static final MethodHandle MH_addCharAndCharsFromNestedStruct;
	public static final MethodHandle MH_addCharAndCharsFromNestedStruct_reverseOrder;
	public static final MethodHandle MH_addCharAndCharsFromStructWithNestedCharArray;
	public static final MethodHandle MH_addCharAndCharsFromStructWithNestedCharArray_reverseOrder;
	public static final MethodHandle MH_addCharAndCharsFromStructWithNestedStructArray;
	public static final MethodHandle MH_addCharAndCharsFromStructWithNestedStructArray_reverseOrder;
	public static final MethodHandle MH_add2CharStructs_returnStruct;
	public static final MethodHandle MH_add2CharStructs_returnStructPointer;
	public static final MethodHandle MH_add3CharStructs_returnStruct;

	public static final MethodHandle MH_addShortAndShortsFromStruct;
	public static final MethodHandle MH_addShortAnd10ShortsFromStruct;
	public static final MethodHandle MH_addShortFromPointerAndShortsFromStruct;
	public static final MethodHandle MH_addShortFromPointerAndShortsFromStruct_returnShortPointer;
	public static final MethodHandle MH_addShortAndShortsFromStructPointer;
	public static final MethodHandle MH_addShortAndShortsFromNestedStruct;
	public static final MethodHandle MH_addShortAndShortsFromNestedStruct_reverseOrder;
	public static final MethodHandle MH_addShortAndShortsFromStructWithNestedShortArray;
	public static final MethodHandle MH_addShortAndShortsFromStructWithNestedShortArray_reverseOrder;
	public static final MethodHandle MH_addShortAndShortsFromStructWithNestedStructArray;
	public static final MethodHandle MH_addShortAndShortsFromStructWithNestedStructArray_reverseOrder;
	public static final MethodHandle MH_add2ShortStructs_returnStruct;
	public static final MethodHandle MH_add2ShortStructs_returnStructPointer;
	public static final MethodHandle MH_add3ShortStructs_returnStruct;

	public static final MethodHandle MH_addIntAndIntsFromStruct;
	public static final MethodHandle MH_addIntAnd5IntsFromStruct;
	public static final MethodHandle MH_addIntFromPointerAndIntsFromStruct;
	public static final MethodHandle MH_addIntFromPointerAndIntsFromStruct_returnIntPointer;
	public static final MethodHandle MH_addIntAndIntsFromStructPointer;
	public static final MethodHandle MH_addIntAndIntsFromNestedStruct;
	public static final MethodHandle MH_addIntAndIntsFromNestedStruct_reverseOrder;
	public static final MethodHandle MH_addIntAndIntsFromStructWithNestedIntArray;
	public static final MethodHandle MH_addIntAndIntsFromStructWithNestedIntArray_reverseOrder;
	public static final MethodHandle MH_addIntAndIntsFromStructWithNestedStructArray;
	public static final MethodHandle MH_addIntAndIntsFromStructWithNestedStructArray_reverseOrder;
	public static final MethodHandle MH_add2IntStructs_returnStruct;
	public static final MethodHandle MH_add2IntStructs_returnStruct_throwException;
	public static final MethodHandle MH_add2IntStructs_returnStruct_nestedUpcall;
	public static final MethodHandle MH_add2IntStructs_returnStruct_nullValue;
	public static final MethodHandle MH_add2IntStructs_returnStruct_nullSegmt;
	public static final MethodHandle MH_add2IntStructs_returnStruct_heapSegmt;
	public static final MethodHandle MH_add2IntStructs_returnStructPointer;
	public static final MethodHandle MH_add2IntStructs_returnStructPointer_nullValue;
	public static final MethodHandle MH_add2IntStructs_returnStructPointer_nullSegmt;
	public static final MethodHandle MH_add2IntStructs_returnStructPointer_heapSegmt;
	public static final MethodHandle MH_add3IntStructs_returnStruct;

	public static final MethodHandle MH_addLongAndLongsFromStruct;
	public static final MethodHandle MH_addLongFromPointerAndLongsFromStruct;
	public static final MethodHandle MH_addLongFromPointerAndLongsFromStruct_returnLongPointer;
	public static final MethodHandle MH_addLongAndLongsFromStructPointer;
	public static final MethodHandle MH_addLongAndLongsFromNestedStruct;
	public static final MethodHandle MH_addLongAndLongsFromNestedStruct_reverseOrder;
	public static final MethodHandle MH_addLongAndLongsFromStructWithNestedLongArray;
	public static final MethodHandle MH_addLongAndLongsFromStructWithNestedLongArray_reverseOrder;
	public static final MethodHandle MH_addLongAndLongsFromStructWithNestedStructArray;
	public static final MethodHandle MH_addLongAndLongsFromStructWithNestedStructArray_reverseOrder;
	public static final MethodHandle MH_add2LongStructs_returnStruct;
	public static final MethodHandle MH_add2LongStructs_returnStructPointer;
	public static final MethodHandle MH_add3LongStructs_returnStruct;

	public static final MethodHandle MH_addFloatAndFloatsFromStruct;
	public static final MethodHandle MH_addFloatAnd5FloatsFromStruct;
	public static final MethodHandle MH_addFloatFromPointerAndFloatsFromStruct;
	public static final MethodHandle MH_addFloatFromPointerAndFloatsFromStruct_returnFloatPointer;
	public static final MethodHandle MH_addFloatAndFloatsFromStructPointer;
	public static final MethodHandle MH_addFloatAndFloatsFromNestedStruct;
	public static final MethodHandle MH_addFloatAndFloatsFromNestedStruct_reverseOrder;
	public static final MethodHandle MH_addFloatAndFloatsFromStructWithNestedFloatArray;
	public static final MethodHandle MH_addFloatAndFloatsFromStructWithNestedFloatArray_reverseOrder;
	public static final MethodHandle MH_addFloatAndFloatsFromStructWithNestedStructArray;
	public static final MethodHandle MH_addFloatAndFloatsFromStructWithNestedStructArray_reverseOrder;
	public static final MethodHandle MH_add2FloatStructs_returnStruct;
	public static final MethodHandle MH_add2FloatStructs_returnStructPointer;
	public static final MethodHandle MH_add3FloatStructs_returnStruct;

	public static final MethodHandle MH_addDoubleAndDoublesFromStruct;
	public static final MethodHandle MH_addDoubleFromPointerAndDoublesFromStruct;
	public static final MethodHandle MH_addDoubleFromPointerAndDoublesFromStruct_returnDoublePointer;
	public static final MethodHandle MH_addDoubleAndDoublesFromStructPointer;
	public static final MethodHandle MH_addDoubleAndDoublesFromNestedStruct;
	public static final MethodHandle MH_addDoubleAndDoublesFromNestedStruct_reverseOrder;
	public static final MethodHandle MH_addDoubleAndDoublesFromStructWithNestedDoubleArray;
	public static final MethodHandle MH_addDoubleAndDoublesFromStructWithNestedDoubleArray_reverseOrder;
	public static final MethodHandle MH_addDoubleAndDoublesFromStructWithNestedStructArray;
	public static final MethodHandle MH_addDoubleAndDoublesFromStructWithNestedStructArray_reverseOrder;
	public static final MethodHandle MH_add2DoubleStructs_returnStruct;
	public static final MethodHandle MH_add2DoubleStructs_returnStructPointer;
	public static final MethodHandle MH_add3DoubleStructs_returnStruct;

	public static final MethodHandle MH_addIntAndIntShortFromStruct;
	public static final MethodHandle MH_addIntAndShortIntFromStruct;
	public static final MethodHandle MH_addIntAndIntLongFromStruct;
	public static final MethodHandle MH_addIntAndLongIntFromStruct;
	public static final MethodHandle MH_addDoubleAndIntDoubleFromStruct;
	public static final MethodHandle MH_addDoubleAndDoubleIntFromStruct;
	public static final MethodHandle MH_addDoubleAndFloatDoubleFromStruct;
	public static final MethodHandle MH_addDoubleAndDoubleFloatFromStruct;
	public static final MethodHandle MH_addDoubleAnd2FloatsDoubleFromStruct;
	public static final MethodHandle MH_addDoubleAndDouble2FloatsFromStruct;
	public static final MethodHandle MH_addFloatAndInt2FloatsFromStruct;
	public static final MethodHandle MH_addFloatAndFloatIntFloatFromStruct;
	public static final MethodHandle MH_addDoubleAndIntFloatDoubleFromStruct;
	public static final MethodHandle MH_addDoubleAndFloatIntDoubleFromStruct;
	public static final MethodHandle MH_addDoubleAndLongDoubleFromStruct;
	public static final MethodHandle MH_addFloatAndInt3FloatsFromStruct;
	public static final MethodHandle MH_addLongAndLong2FloatsFromStruct;
	public static final MethodHandle MH_addFloatAnd3FloatsIntFromStruct;
	public static final MethodHandle MH_addLongAndFloatLongFromStruct;
	public static final MethodHandle MH_addDoubleAndDoubleFloatIntFromStruct;
	public static final MethodHandle MH_addDoubleAndDoubleLongFromStruct;
	public static final MethodHandle MH_addLongAnd2FloatsLongFromStruct;
	public static final MethodHandle MH_addShortAnd3ShortsCharFromStruct;
	public static final MethodHandle MH_addFloatAndIntFloatIntFloatFromStruct;
	public static final MethodHandle MH_addDoubleAndIntDoubleFloatFromStruct;
	public static final MethodHandle MH_addDoubleAndFloatDoubleIntFromStruct;
	public static final MethodHandle MH_addDoubleAndIntDoubleIntFromStruct;
	public static final MethodHandle MH_addDoubleAndFloatDoubleFloatFromStruct;
	public static final MethodHandle MH_addDoubleAndIntDoubleLongFromStruct;
	public static final MethodHandle MH_return254BytesFromStruct;
	public static final MethodHandle MH_return4KBytesFromStruct;

	public static final MethodHandle MH_addNegBytesFromStruct;
	public static final MethodHandle MH_addNegShortsFromStruct;
	public static final MethodHandle MH_captureTrivialOption;

	private static Linker linker = Linker.nativeLinker();

	static {
		System.loadLibrary("clinkerffitests");

		try {
			MH_add2BoolsWithOr = lookup.findStatic(UpcallMethodHandles.class, "add2BoolsWithOr", methodType(boolean.class, boolean.class, boolean.class)); //$NON-NLS-1$
			MH_addBoolAndBoolFromPointerWithOr = lookup.findStatic(UpcallMethodHandles.class, "addBoolAndBoolFromPointerWithOr", methodType(boolean.class, boolean.class, MemorySegment.class)); //$NON-NLS-1$
			MH_addBoolAndBoolFromPtrWithOr_RetPtr = lookup.findStatic(UpcallMethodHandles.class, "addBoolAndBoolFromPtrWithOr_RetPtr", MT_Segmt_Bool_MemSegmt); //$NON-NLS-1$
			MH_addBoolAndBoolFromPtrWithOr_RetArgPtr = lookup.findStatic(UpcallMethodHandles.class, "addBoolAndBoolFromPtrWithOr_RetArgPtr", MT_Segmt_Bool_MemSegmt); //$NON-NLS-1$

			MH_createNewCharFrom2Chars = lookup.findStatic(UpcallMethodHandles.class, "createNewCharFrom2Chars", methodType(char.class, char.class, char.class)); //$NON-NLS-1$
			MH_createNewCharFromCharAndCharFromPointer = lookup.findStatic(UpcallMethodHandles.class, "createNewCharFromCharAndCharFromPointer", methodType(char.class, MemorySegment.class, char.class)); //$NON-NLS-1$
			MH_createNewCharFromCharAndCharFromPtr_RetPtr = lookup.findStatic(UpcallMethodHandles.class, "createNewCharFromCharAndCharFromPtr_RetPtr", MT_Segmt_MemSegmt_Char); //$NON-NLS-1$
			MH_createNewCharFromCharAndCharFromPtr_RetArgPtr = lookup.findStatic(UpcallMethodHandles.class, "createNewCharFromCharAndCharFromPtr_RetArgPtr", MT_Segmt_MemSegmt_Char); //$NON-NLS-1$

			MH_add2Bytes = lookup.findStatic(UpcallMethodHandles.class, "add2Bytes", methodType(byte.class, byte.class, byte.class)); //$NON-NLS-1$
			MH_addByteAndByteFromPointer = lookup.findStatic(UpcallMethodHandles.class, "addByteAndByteFromPointer", methodType(byte.class, byte.class, MemorySegment.class)); //$NON-NLS-1$
			MH_addByteAndByteFromPtr_RetPtr = lookup.findStatic(UpcallMethodHandles.class, "addByteAndByteFromPtr_RetPtr", MT_Segmt_Byte_MemSegmt); //$NON-NLS-1$
			MH_addByteAndByteFromPtr_RetArgPtr = lookup.findStatic(UpcallMethodHandles.class, "addByteAndByteFromPtr_RetArgPtr", MT_Segmt_Byte_MemSegmt); //$NON-NLS-1$

			MH_add2Shorts = lookup.findStatic(UpcallMethodHandles.class, "add2Shorts", methodType(short.class, short.class, short.class)); //$NON-NLS-1$
			MH_addShortAndShortFromPointer = lookup.findStatic(UpcallMethodHandles.class, "addShortAndShortFromPointer", methodType(short.class, MemorySegment.class, short.class)); //$NON-NLS-1$
			MH_addShortAndShortFromPtr_RetPtr = lookup.findStatic(UpcallMethodHandles.class, "addShortAndShortFromPtr_RetPtr", MT_Segmt_MemSegmt_Short); //$NON-NLS-1$
			MH_addShortAndShortFromPtr_RetArgPtr = lookup.findStatic(UpcallMethodHandles.class, "addShortAndShortFromPtr_RetArgPtr", MT_Segmt_MemSegmt_Short); //$NON-NLS-1$

			MH_add2Ints = lookup.findStatic(UpcallMethodHandles.class, "add2Ints", methodType(int.class, int.class, int.class)); //$NON-NLS-1$
			MH_addIntAndIntFromPointer = lookup.findStatic(UpcallMethodHandles.class, "addIntAndIntFromPointer", methodType(int.class, int.class, MemorySegment.class)); //$NON-NLS-1$
			MH_addIntAndIntFromPtr_RetPtr = lookup.findStatic(UpcallMethodHandles.class, "addIntAndIntFromPtr_RetPtr", MT_Segmt_Int_MemSegmt); //$NON-NLS-1$
			MH_addIntAndIntFromPtr_RetArgPtr = lookup.findStatic(UpcallMethodHandles.class, "addIntAndIntFromPtr_RetArgPtr", MT_Segmt_Int_MemSegmt); //$NON-NLS-1$
			MH_add3Ints = lookup.findStatic(UpcallMethodHandles.class, "add3Ints", methodType(int.class, int.class, int.class, int.class)); //$NON-NLS-1$
			MH_addIntAndChar = lookup.findStatic(UpcallMethodHandles.class, "addIntAndChar", methodType(int.class, int.class, char.class)); //$NON-NLS-1$
			MH_add2IntsReturnVoid = lookup.findStatic(UpcallMethodHandles.class, "add2IntsReturnVoid", methodType(void.class, int.class, int.class)); //$NON-NLS-1$

			MH_add2Longs = lookup.findStatic(UpcallMethodHandles.class, "add2Longs", methodType(long.class, long.class, long.class)); //$NON-NLS-1$
			MH_addLongAndLongFromPointer = lookup.findStatic(UpcallMethodHandles.class, "addLongAndLongFromPointer", methodType(long.class, MemorySegment.class, long.class)); //$NON-NLS-1$
			MH_addLongAndLongFromPtr_RetPtr = lookup.findStatic(UpcallMethodHandles.class, "addLongAndLongFromPtr_RetPtr", MT_Segmt_MemSegmt_Long); //$NON-NLS-1$
			MH_addLongAndLongFromPtr_RetArgPtr = lookup.findStatic(UpcallMethodHandles.class, "addLongAndLongFromPtr_RetArgPtr", MT_Segmt_MemSegmt_Long); //$NON-NLS-1$

			MH_add2Floats = lookup.findStatic(UpcallMethodHandles.class, "add2Floats", methodType(float.class, float.class, float.class)); //$NON-NLS-1$
			MH_addFloatAndFloatFromPointer = lookup.findStatic(UpcallMethodHandles.class, "addFloatAndFloatFromPointer", methodType(float.class, float.class, MemorySegment.class)); //$NON-NLS-1$
			MH_addFloatAndFloatFromPtr_RetPtr = lookup.findStatic(UpcallMethodHandles.class, "addFloatAndFloatFromPtr_RetPtr", MT_Segmt_Float_MemSegmt); //$NON-NLS-1$
			MH_addFloatAndFloatFromPtr_RetArgPtr = lookup.findStatic(UpcallMethodHandles.class, "addFloatAndFloatFromPtr_RetArgPtr", MT_Segmt_Float_MemSegmt); //$NON-NLS-1$

			MH_add2Doubles = lookup.findStatic(UpcallMethodHandles.class, "add2Doubles", methodType(double.class, double.class, double.class)); //$NON-NLS-1$
			MH_addDoubleAndDoubleFromPointer = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndDoubleFromPointer", methodType(double.class, MemorySegment.class, double.class)); //$NON-NLS-1$
			MH_addDoubleAndDoubleFromPtr_RetPtr = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndDoubleFromPtr_RetPtr", MT_Segmt_MemSegmt_Double); //$NON-NLS-1$
			MH_addDoubleAndDoubleFromPtr_RetArgPtr = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndDoubleFromPtr_RetArgPtr", MT_Segmt_MemSegmt_Double); //$NON-NLS-1$

			MH_compare = lookup.findStatic(UpcallMethodHandles.class, "compare", methodType(int.class, MemorySegment.class, MemorySegment.class)); //$NON-NLS-1$

			MH_addBoolAndBoolsFromStructWithXor = lookup.findStatic(UpcallMethodHandles.class, "addBoolAndBoolsFromStructWithXor", MT_Bool_Bool_MemSegmt); //$NON-NLS-1$
			MH_addBoolAnd20BoolsFromStructWithXor = lookup.findStatic(UpcallMethodHandles.class, "addBoolAnd20BoolsFromStructWithXor", MT_Bool_Bool_MemSegmt); //$NON-NLS-1$
			MH_addBoolFromPointerAndBoolsFromStructWithXor = lookup.findStatic(UpcallMethodHandles.class, "addBoolFromPointerAndBoolsFromStructWithXor", methodType(boolean.class, MemorySegment.class, MemorySegment.class)); //$NON-NLS-1$
			MH_addBoolFromPointerAndBoolsFromStructWithXor_returnBoolPointer = lookup.findStatic(UpcallMethodHandles.class, "addBoolFromPointerAndBoolsFromStructWithXor_returnBoolPointer", MT_Segmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_addBoolAndBoolsFromStructPointerWithXor = lookup.findStatic(UpcallMethodHandles.class, "addBoolAndBoolsFromStructPointerWithXor", methodType(boolean.class, boolean.class, MemorySegment.class)); //$NON-NLS-1$
			MH_addBoolAndBoolsFromNestedStructWithXor = lookup.findStatic(UpcallMethodHandles.class, "addBoolAndBoolsFromNestedStructWithXor", MT_Bool_Bool_MemSegmt); //$NON-NLS-1$
			MH_addBoolAndBoolsFromNestedStructWithXor_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addBoolAndBoolsFromNestedStructWithXor_reverseOrder", MT_Bool_Bool_MemSegmt); //$NON-NLS-1$
			MH_addBoolAndBoolsFromStructWithNestedBoolArray = lookup.findStatic(UpcallMethodHandles.class, "addBoolAndBoolsFromStructWithNestedBoolArray", MT_Bool_Bool_MemSegmt); //$NON-NLS-1$
			MH_addBoolAndBoolsFromStructWithNestedBoolArray_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addBoolAndBoolsFromStructWithNestedBoolArray_reverseOrder", MT_Bool_Bool_MemSegmt); //$NON-NLS-1$
			MH_addBoolAndBoolsFromStructWithNestedStructArray = lookup.findStatic(UpcallMethodHandles.class, "addBoolAndBoolsFromStructWithNestedStructArray", MT_Bool_Bool_MemSegmt); //$NON-NLS-1$
			MH_addBoolAndBoolsFromStructWithNestedStructArray_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addBoolAndBoolsFromStructWithNestedStructArray_reverseOrder", MT_Bool_Bool_MemSegmt); //$NON-NLS-1$
			MH_add2BoolStructsWithXor_returnStruct = lookup.findStatic(UpcallMethodHandles.class, "add2BoolStructsWithXor_returnStruct", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add2BoolStructsWithXor_returnStructPointer = lookup.findStatic(UpcallMethodHandles.class, "add2BoolStructsWithXor_returnStructPointer", MT_Segmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add3BoolStructsWithXor_returnStruct = lookup.findStatic(UpcallMethodHandles.class, "add3BoolStructsWithXor_returnStruct", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$

			MH_addByteAndBytesFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addByteAndBytesFromStruct", MT_Byte_Byte_MemSegmt); //$NON-NLS-1$
			MH_addByteAnd20BytesFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addByteAnd20BytesFromStruct", MT_Byte_Byte_MemSegmt); //$NON-NLS-1$
			MH_addByteFromPointerAndBytesFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addByteFromPointerAndBytesFromStruct", methodType(byte.class, MemorySegment.class, MemorySegment.class)); //$NON-NLS-1$
			MH_addByteFromPointerAndBytesFromStruct_returnBytePointer = lookup.findStatic(UpcallMethodHandles.class, "addByteFromPointerAndBytesFromStruct_returnBytePointer", MT_Segmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_addByteAndBytesFromStructPointer = lookup.findStatic(UpcallMethodHandles.class, "addByteAndBytesFromStructPointer", methodType(byte.class, byte.class, MemorySegment.class)); //$NON-NLS-1$
			MH_addByteAndBytesFromNestedStruct = lookup.findStatic(UpcallMethodHandles.class, "addByteAndBytesFromNestedStruct", MT_Byte_Byte_MemSegmt); //$NON-NLS-1$
			MH_addByteAndBytesFromNestedStruct_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addByteAndBytesFromNestedStruct_reverseOrder", MT_Byte_Byte_MemSegmt); //$NON-NLS-1$
			MH_addByteAndBytesFromStructWithNestedByteArray = lookup.findStatic(UpcallMethodHandles.class, "addByteAndBytesFromStructWithNestedByteArray", MT_Byte_Byte_MemSegmt); //$NON-NLS-1$
			MH_addByteAndBytesFromStructWithNestedByteArray_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addByteAndBytesFromStructWithNestedByteArray_reverseOrder", MT_Byte_Byte_MemSegmt); //$NON-NLS-1$
			MH_addByteAndBytesFromStructWithNestedStructArray = lookup.findStatic(UpcallMethodHandles.class, "addByteAndBytesFromStructWithNestedStructArray", MT_Byte_Byte_MemSegmt); //$NON-NLS-1$
			MH_addByteAndBytesFromStructWithNestedStructArray_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addByteAndBytesFromStructWithNestedStructArray_reverseOrder", MT_Byte_Byte_MemSegmt); //$NON-NLS-1$
			MH_add1ByteStructs_returnStruct = lookup.findStatic(UpcallMethodHandles.class, "add1ByteStructs_returnStruct", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add2ByteStructs_returnStruct = lookup.findStatic(UpcallMethodHandles.class, "add2ByteStructs_returnStruct", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add2ByteStructs_returnStructPointer = lookup.findStatic(UpcallMethodHandles.class, "add2ByteStructs_returnStructPointer", MT_Segmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add3ByteStructs_returnStruct = lookup.findStatic(UpcallMethodHandles.class, "add3ByteStructs_returnStruct", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$

			MH_addCharAndCharsFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addCharAndCharsFromStruct", MT_Char_Char_MemSegmt); //$NON-NLS-1$
			MH_addCharAnd10CharsFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addCharAnd10CharsFromStruct", MT_Char_Char_MemSegmt); //$NON-NLS-1$
			MH_addCharFromPointerAndCharsFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addCharFromPointerAndCharsFromStruct", methodType(char.class, MemorySegment.class, MemorySegment.class)); //$NON-NLS-1$
			MH_addCharFromPointerAndCharsFromStruct_returnCharPointer = lookup.findStatic(UpcallMethodHandles.class, "addCharFromPointerAndCharsFromStruct_returnCharPointer", MT_Segmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_addCharAndCharsFromStructPointer = lookup.findStatic(UpcallMethodHandles.class, "addCharAndCharsFromStructPointer", methodType(char.class, char.class, MemorySegment.class)); //$NON-NLS-1$
			MH_addCharAndCharsFromNestedStruct = lookup.findStatic(UpcallMethodHandles.class, "addCharAndCharsFromNestedStruct", MT_Char_Char_MemSegmt); //$NON-NLS-1$
			MH_addCharAndCharsFromNestedStruct_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addCharAndCharsFromNestedStruct_reverseOrder", MT_Char_Char_MemSegmt); //$NON-NLS-1$
			MH_addCharAndCharsFromStructWithNestedCharArray = lookup.findStatic(UpcallMethodHandles.class, "addCharAndCharsFromStructWithNestedCharArray", MT_Char_Char_MemSegmt); //$NON-NLS-1$
			MH_addCharAndCharsFromStructWithNestedCharArray_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addCharAndCharsFromStructWithNestedCharArray_reverseOrder", MT_Char_Char_MemSegmt); //$NON-NLS-1$
			MH_addCharAndCharsFromStructWithNestedStructArray = lookup.findStatic(UpcallMethodHandles.class, "addCharAndCharsFromStructWithNestedStructArray", MT_Char_Char_MemSegmt); //$NON-NLS-1$
			MH_addCharAndCharsFromStructWithNestedStructArray_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addCharAndCharsFromStructWithNestedStructArray_reverseOrder", MT_Char_Char_MemSegmt); //$NON-NLS-1$
			MH_add2CharStructs_returnStruct = lookup.findStatic(UpcallMethodHandles.class, "add2CharStructs_returnStruct", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add2CharStructs_returnStructPointer = lookup.findStatic(UpcallMethodHandles.class, "add2CharStructs_returnStructPointer", MT_Segmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add3CharStructs_returnStruct = lookup.findStatic(UpcallMethodHandles.class, "add3CharStructs_returnStruct", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$

			MH_addShortAndShortsFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addShortAndShortsFromStruct", MT_Short_Short_MemSegmt); //$NON-NLS-1$
			MH_addShortAnd10ShortsFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addShortAnd10ShortsFromStruct", MT_Short_Short_MemSegmt); //$NON-NLS-1$
			MH_addShortFromPointerAndShortsFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addShortFromPointerAndShortsFromStruct", methodType(short.class, MemorySegment.class, MemorySegment.class)); //$NON-NLS-1$
			MH_addShortFromPointerAndShortsFromStruct_returnShortPointer = lookup.findStatic(UpcallMethodHandles.class, "addShortFromPointerAndShortsFromStruct_returnShortPointer", MT_Segmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_addShortAndShortsFromStructPointer = lookup.findStatic(UpcallMethodHandles.class, "addShortAndShortsFromStructPointer", methodType(short.class, short.class, MemorySegment.class)); //$NON-NLS-1$
			MH_addShortAndShortsFromNestedStruct = lookup.findStatic(UpcallMethodHandles.class, "addShortAndShortsFromNestedStruct", MT_Short_Short_MemSegmt); //$NON-NLS-1$
			MH_addShortAndShortsFromNestedStruct_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addShortAndShortsFromNestedStruct_reverseOrder", MT_Short_Short_MemSegmt); //$NON-NLS-1$
			MH_addShortAndShortsFromStructWithNestedShortArray = lookup.findStatic(UpcallMethodHandles.class, "addShortAndShortsFromStructWithNestedShortArray", MT_Short_Short_MemSegmt); //$NON-NLS-1$
			MH_addShortAndShortsFromStructWithNestedShortArray_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addShortAndShortsFromStructWithNestedShortArray_reverseOrder", MT_Short_Short_MemSegmt); //$NON-NLS-1$
			MH_addShortAndShortsFromStructWithNestedStructArray = lookup.findStatic(UpcallMethodHandles.class, "addShortAndShortsFromStructWithNestedStructArray", MT_Short_Short_MemSegmt); //$NON-NLS-1$
			MH_addShortAndShortsFromStructWithNestedStructArray_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addShortAndShortsFromStructWithNestedStructArray_reverseOrder", MT_Short_Short_MemSegmt); //$NON-NLS-1$
			MH_add2ShortStructs_returnStruct = lookup.findStatic(UpcallMethodHandles.class, "add2ShortStructs_returnStruct", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add2ShortStructs_returnStructPointer = lookup.findStatic(UpcallMethodHandles.class, "add2ShortStructs_returnStructPointer", MT_Segmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add3ShortStructs_returnStruct = lookup.findStatic(UpcallMethodHandles.class, "add3ShortStructs_returnStruct", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$

			MH_addIntAndIntsFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addIntAndIntsFromStruct", MT_Int_Int_MemSegmt); //$NON-NLS-1$
			MH_addIntAnd5IntsFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addIntAnd5IntsFromStruct", MT_Int_Int_MemSegmt); //$NON-NLS-1$
			MH_addIntFromPointerAndIntsFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addIntFromPointerAndIntsFromStruct", methodType(int.class, MemorySegment.class, MemorySegment.class)); //$NON-NLS-1$
			MH_addIntFromPointerAndIntsFromStruct_returnIntPointer = lookup.findStatic(UpcallMethodHandles.class, "addIntFromPointerAndIntsFromStruct_returnIntPointer", MT_Segmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_addIntAndIntsFromStructPointer = lookup.findStatic(UpcallMethodHandles.class, "addIntAndIntsFromStructPointer", methodType(int.class, int.class, MemorySegment.class)); //$NON-NLS-1$
			MH_addIntAndIntsFromNestedStruct = lookup.findStatic(UpcallMethodHandles.class, "addIntAndIntsFromNestedStruct", MT_Int_Int_MemSegmt); //$NON-NLS-1$
			MH_addIntAndIntsFromNestedStruct_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addIntAndIntsFromNestedStruct_reverseOrder", MT_Int_Int_MemSegmt); //$NON-NLS-1$
			MH_addIntAndIntsFromStructWithNestedIntArray = lookup.findStatic(UpcallMethodHandles.class, "addIntAndIntsFromStructWithNestedIntArray", MT_Int_Int_MemSegmt); //$NON-NLS-1$
			MH_addIntAndIntsFromStructWithNestedIntArray_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addIntAndIntsFromStructWithNestedIntArray_reverseOrder", MT_Int_Int_MemSegmt); //$NON-NLS-1$
			MH_addIntAndIntsFromStructWithNestedStructArray = lookup.findStatic(UpcallMethodHandles.class, "addIntAndIntsFromStructWithNestedStructArray", MT_Int_Int_MemSegmt); //$NON-NLS-1$
			MH_addIntAndIntsFromStructWithNestedStructArray_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addIntAndIntsFromStructWithNestedStructArray_reverseOrder", MT_Int_Int_MemSegmt); //$NON-NLS-1$
			MH_add2IntStructs_returnStruct = lookup.findStatic(UpcallMethodHandles.class, "add2IntStructs_returnStruct", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add2IntStructs_returnStruct_throwException = lookup.findStatic(UpcallMethodHandles.class, "add2IntStructs_returnStruct_throwException", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add2IntStructs_returnStruct_nestedUpcall = lookup.findStatic(UpcallMethodHandles.class, "add2IntStructs_returnStruct_nestedUpcall", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add2IntStructs_returnStruct_nullValue = lookup.findStatic(UpcallMethodHandles.class, "add2IntStructs_returnStruct_nullValue", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add2IntStructs_returnStruct_nullSegmt = lookup.findStatic(UpcallMethodHandles.class, "add2IntStructs_returnStruct_nullSegmt", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add2IntStructs_returnStruct_heapSegmt = lookup.findStatic(UpcallMethodHandles.class, "add2IntStructs_returnStruct_heapSegmt", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add2IntStructs_returnStructPointer = lookup.findStatic(UpcallMethodHandles.class, "add2IntStructs_returnStructPointer", MT_Segmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add2IntStructs_returnStructPointer_nullValue = lookup.findStatic(UpcallMethodHandles.class, "add2IntStructs_returnStructPointer_nullValue", MT_Segmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add2IntStructs_returnStructPointer_nullSegmt = lookup.findStatic(UpcallMethodHandles.class, "add2IntStructs_returnStructPointer_nullSegmt", MT_Segmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add2IntStructs_returnStructPointer_heapSegmt = lookup.findStatic(UpcallMethodHandles.class, "add2IntStructs_returnStructPointer_heapSegmt", MT_Segmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add3IntStructs_returnStruct = lookup.findStatic(UpcallMethodHandles.class, "add3IntStructs_returnStruct", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$

			MH_addLongAndLongsFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addLongAndLongsFromStruct", MT_Long_Long_MemSegmt); //$NON-NLS-1$
			MH_addLongFromPointerAndLongsFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addLongFromPointerAndLongsFromStruct", methodType(long.class, MemorySegment.class, MemorySegment.class)); //$NON-NLS-1$
			MH_addLongFromPointerAndLongsFromStruct_returnLongPointer = lookup.findStatic(UpcallMethodHandles.class, "addLongFromPointerAndLongsFromStruct_returnLongPointer", MT_Segmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_addLongAndLongsFromStructPointer = lookup.findStatic(UpcallMethodHandles.class, "addLongAndLongsFromStructPointer", methodType(long.class, long.class, MemorySegment.class)); //$NON-NLS-1$
			MH_addLongAndLongsFromNestedStruct = lookup.findStatic(UpcallMethodHandles.class, "addLongAndLongsFromNestedStruct", MT_Long_Long_MemSegmt); //$NON-NLS-1$
			MH_addLongAndLongsFromNestedStruct_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addLongAndLongsFromNestedStruct_reverseOrder", MT_Long_Long_MemSegmt); //$NON-NLS-1$
			MH_addLongAndLongsFromStructWithNestedLongArray = lookup.findStatic(UpcallMethodHandles.class, "addLongAndLongsFromStructWithNestedLongArray", MT_Long_Long_MemSegmt); //$NON-NLS-1$
			MH_addLongAndLongsFromStructWithNestedLongArray_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addLongAndLongsFromStructWithNestedLongArray_reverseOrder", MT_Long_Long_MemSegmt); //$NON-NLS-1$
			MH_addLongAndLongsFromStructWithNestedStructArray = lookup.findStatic(UpcallMethodHandles.class, "addLongAndLongsFromStructWithNestedStructArray", MT_Long_Long_MemSegmt); //$NON-NLS-1$
			MH_addLongAndLongsFromStructWithNestedStructArray_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addLongAndLongsFromStructWithNestedStructArray_reverseOrder", MT_Long_Long_MemSegmt); //$NON-NLS-1$
			MH_add2LongStructs_returnStruct = lookup.findStatic(UpcallMethodHandles.class, "add2LongStructs_returnStruct", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add2LongStructs_returnStructPointer = lookup.findStatic(UpcallMethodHandles.class, "add2LongStructs_returnStructPointer", MT_Segmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add3LongStructs_returnStruct = lookup.findStatic(UpcallMethodHandles.class, "add3LongStructs_returnStruct", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$

			MH_addFloatAndFloatsFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addFloatAndFloatsFromStruct", MT_Float_Float_MemSegmt); //$NON-NLS-1$
			MH_addFloatAnd5FloatsFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addFloatAnd5FloatsFromStruct", MT_Float_Float_MemSegmt); //$NON-NLS-1$
			MH_addFloatFromPointerAndFloatsFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addFloatFromPointerAndFloatsFromStruct", methodType(float.class, MemorySegment.class, MemorySegment.class)); //$NON-NLS-1$
			MH_addFloatFromPointerAndFloatsFromStruct_returnFloatPointer = lookup.findStatic(UpcallMethodHandles.class, "addFloatFromPointerAndFloatsFromStruct_returnFloatPointer", MT_Segmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_addFloatAndFloatsFromStructPointer = lookup.findStatic(UpcallMethodHandles.class, "addFloatAndFloatsFromStructPointer", methodType(float.class, float.class, MemorySegment.class)); //$NON-NLS-1$
			MH_addFloatAndFloatsFromNestedStruct = lookup.findStatic(UpcallMethodHandles.class, "addFloatAndFloatsFromNestedStruct", MT_Float_Float_MemSegmt); //$NON-NLS-1$
			MH_addFloatAndFloatsFromNestedStruct_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addFloatAndFloatsFromNestedStruct_reverseOrder", MT_Float_Float_MemSegmt); //$NON-NLS-1$
			MH_addFloatAndFloatsFromStructWithNestedFloatArray = lookup.findStatic(UpcallMethodHandles.class, "addFloatAndFloatsFromStructWithNestedFloatArray", MT_Float_Float_MemSegmt); //$NON-NLS-1$
			MH_addFloatAndFloatsFromStructWithNestedFloatArray_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addFloatAndFloatsFromStructWithNestedFloatArray_reverseOrder", MT_Float_Float_MemSegmt); //$NON-NLS-1$
			MH_addFloatAndFloatsFromStructWithNestedStructArray = lookup.findStatic(UpcallMethodHandles.class, "addFloatAndFloatsFromStructWithNestedStructArray", MT_Float_Float_MemSegmt); //$NON-NLS-1$
			MH_addFloatAndFloatsFromStructWithNestedStructArray_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addFloatAndFloatsFromStructWithNestedStructArray_reverseOrder", MT_Float_Float_MemSegmt); //$NON-NLS-1$
			MH_add2FloatStructs_returnStruct = lookup.findStatic(UpcallMethodHandles.class, "add2FloatStructs_returnStruct", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add2FloatStructs_returnStructPointer = lookup.findStatic(UpcallMethodHandles.class, "add2FloatStructs_returnStructPointer", MT_Segmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add3FloatStructs_returnStruct = lookup.findStatic(UpcallMethodHandles.class, "add3FloatStructs_returnStruct", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$

			MH_addDoubleAndDoublesFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndDoublesFromStruct", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addDoubleFromPointerAndDoublesFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addDoubleFromPointerAndDoublesFromStruct", methodType(double.class, MemorySegment.class, MemorySegment.class)); //$NON-NLS-1$
			MH_addDoubleFromPointerAndDoublesFromStruct_returnDoublePointer = lookup.findStatic(UpcallMethodHandles.class, "addDoubleFromPointerAndDoublesFromStruct_returnDoublePointer", MT_Segmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndDoublesFromStructPointer = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndDoublesFromStructPointer", methodType(double.class, double.class, MemorySegment.class)); //$NON-NLS-1$
			MH_addDoubleAndDoublesFromNestedStruct = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndDoublesFromNestedStruct", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndDoublesFromNestedStruct_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndDoublesFromNestedStruct_reverseOrder", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndDoublesFromStructWithNestedDoubleArray = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndDoublesFromStructWithNestedDoubleArray", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndDoublesFromStructWithNestedDoubleArray_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndDoublesFromStructWithNestedDoubleArray_reverseOrder", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndDoublesFromStructWithNestedStructArray = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndDoublesFromStructWithNestedStructArray", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndDoublesFromStructWithNestedStructArray_reverseOrder = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndDoublesFromStructWithNestedStructArray_reverseOrder", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_add2DoubleStructs_returnStruct = lookup.findStatic(UpcallMethodHandles.class, "add2DoubleStructs_returnStruct", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add2DoubleStructs_returnStructPointer = lookup.findStatic(UpcallMethodHandles.class, "add2DoubleStructs_returnStructPointer", MT_Segmt_MemSegmt_MemSegmt); //$NON-NLS-1$
			MH_add3DoubleStructs_returnStruct = lookup.findStatic(UpcallMethodHandles.class, "add3DoubleStructs_returnStruct", MT_MemSegmt_MemSegmt_MemSegmt); //$NON-NLS-1$

			MH_addIntAndIntShortFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addIntAndIntShortFromStruct", MT_Int_Int_MemSegmt); //$NON-NLS-1$
			MH_addIntAndShortIntFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addIntAndShortIntFromStruct", MT_Int_Int_MemSegmt); //$NON-NLS-1$
			MH_addIntAndIntLongFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addIntAndIntLongFromStruct", MT_Long_Int_MemSegmt); //$NON-NLS-1$
			MH_addIntAndLongIntFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addIntAndLongIntFromStruct", MT_Long_Int_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndIntDoubleFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndIntDoubleFromStruct", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndDoubleIntFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndDoubleIntFromStruct", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndFloatDoubleFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndFloatDoubleFromStruct", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndDoubleFloatFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndDoubleFloatFromStruct", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAnd2FloatsDoubleFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAnd2FloatsDoubleFromStruct", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndDouble2FloatsFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndDouble2FloatsFromStruct", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addFloatAndInt2FloatsFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addFloatAndInt2FloatsFromStruct", MT_Float_Float_MemSegmt); //$NON-NLS-1$
			MH_addFloatAndFloatIntFloatFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addFloatAndFloatIntFloatFromStruct", MT_Float_Float_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndIntFloatDoubleFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndIntFloatDoubleFromStruct", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndFloatIntDoubleFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndFloatIntDoubleFromStruct", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndLongDoubleFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndLongDoubleFromStruct", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addFloatAndInt3FloatsFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addFloatAndInt3FloatsFromStruct", MT_Float_Float_MemSegmt); //$NON-NLS-1$
			MH_addLongAndLong2FloatsFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addLongAndLong2FloatsFromStruct", MT_Long_Long_MemSegmt); //$NON-NLS-1$
			MH_addFloatAnd3FloatsIntFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addFloatAnd3FloatsIntFromStruct", MT_Float_Float_MemSegmt); //$NON-NLS-1$
			MH_addLongAndFloatLongFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addLongAndFloatLongFromStruct", MT_Long_Long_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndDoubleFloatIntFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndDoubleFloatIntFromStruct", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndDoubleLongFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndDoubleLongFromStruct", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addLongAnd2FloatsLongFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addLongAnd2FloatsLongFromStruct", MT_Long_Long_MemSegmt); //$NON-NLS-1$
			MH_addShortAnd3ShortsCharFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addShortAnd3ShortsCharFromStruct", MT_Short_Short_MemSegmt); //$NON-NLS-1$
			MH_addFloatAndIntFloatIntFloatFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addFloatAndIntFloatIntFloatFromStruct", MT_Float_Float_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndIntDoubleFloatFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndIntDoubleFloatFromStruct", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndFloatDoubleIntFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndFloatDoubleIntFromStruct", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndIntDoubleIntFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndIntDoubleIntFromStruct", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndFloatDoubleFloatFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndFloatDoubleFloatFromStruct", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_addDoubleAndIntDoubleLongFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addDoubleAndIntDoubleLongFromStruct", MT_Double_Double_MemSegmt); //$NON-NLS-1$
			MH_return254BytesFromStruct = lookup.findStatic(UpcallMethodHandles.class, "return254BytesFromStruct", MT_MemSegmt); //$NON-NLS-1$
			MH_return4KBytesFromStruct = lookup.findStatic(UpcallMethodHandles.class, "return4KBytesFromStruct", MT_MemSegmt); //$NON-NLS-1$

			MH_addNegBytesFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addNegBytesFromStruct", MT_Byte_Byte_MemSegmt.appendParameterTypes(byte.class, byte.class)); //$NON-NLS-1$
			MH_addNegShortsFromStruct = lookup.findStatic(UpcallMethodHandles.class, "addNegShortsFromStruct", MT_Short_Short_MemSegmt.appendParameterTypes(short.class, short.class)); //$NON-NLS-1$
			MH_captureTrivialOption = lookup.findStatic(UpcallMethodHandles.class, "captureTrivialOption", methodType(int.class, int.class)); //$NON-NLS-1$

		} catch (IllegalAccessException | NoSuchMethodException e) {
			throw new InternalError(e);
		}
	}
	private static final SymbolLookup nativeLibLookup = SymbolLookup.loaderLookup();

	public static boolean add2BoolsWithOr(boolean boolArg1, boolean boolArg2) {
		boolean result = boolArg1 || boolArg2;
		return result;
	}

	public static boolean addBoolAndBoolFromPointerWithOr(boolean boolArg1, MemorySegment boolArg2Addr) {
		MemorySegment boolArg2Segmt = boolArg2Addr.reinterpret(JAVA_BOOLEAN.byteSize());
		boolean result = boolArg1 || boolArg2Segmt.get(JAVA_BOOLEAN, 0);
		return result;
	}

	public static MemorySegment addBoolAndBoolFromPtrWithOr_RetPtr(boolean boolArg1, MemorySegment boolArg2Addr) {
		MemorySegment boolArg2Segmt = boolArg2Addr.reinterpret(JAVA_BOOLEAN.byteSize());
		boolean result = boolArg1 || boolArg2Segmt.get(JAVA_BOOLEAN, 0);
		MemorySegment resultSegmt = arena.allocate(JAVA_BOOLEAN);
		resultSegmt.set(JAVA_BOOLEAN, 0, result);
		return resultSegmt;
	}

	public static MemorySegment addBoolAndBoolFromPtrWithOr_RetArgPtr(boolean boolArg1, MemorySegment boolArg2Addr) {
		MemorySegment boolArg2Segmt = boolArg2Addr.reinterpret(JAVA_BOOLEAN.byteSize());
		boolean result = boolArg1 || boolArg2Segmt.get(JAVA_BOOLEAN, 0);
		boolArg2Segmt.set(JAVA_BOOLEAN, 0, result);
		return boolArg2Segmt;
	}

	public static char createNewCharFrom2Chars(char charArg1, char charArg2) {
		int diff = (charArg2 >= charArg1) ? (charArg2 - charArg1) : (charArg1 - charArg2);
		diff = (diff > 5) ? 5 : diff;
		char result = (char)(diff + 'A');
		return result;
	}

	public static char createNewCharFromCharAndCharFromPointer(MemorySegment charArg1Addr, char charArg2) {
		MemorySegment charArg1Segmt = charArg1Addr.reinterpret(JAVA_CHAR.byteSize());
		char charArg1 = charArg1Segmt.get(JAVA_CHAR, 0);
		int diff = (charArg2 >= charArg1) ? (charArg2 - charArg1) : (charArg1 - charArg2);
		diff = (diff > 5) ? 5 : diff;
		char result = (char)(diff + 'A');
		return result;
	}

	public static MemorySegment createNewCharFromCharAndCharFromPtr_RetPtr(MemorySegment charArg1Addr, char charArg2) {
		MemorySegment charArg1Segmt = charArg1Addr.reinterpret(JAVA_CHAR.byteSize());
		char charArg1 = charArg1Segmt.get(JAVA_CHAR, 0);
		int diff = (charArg2 >= charArg1) ? (charArg2 - charArg1) : (charArg1 - charArg2);
		diff = (diff > 5) ? 5 : diff;
		char result = (char)(diff + 'A');
		MemorySegment resultSegmt = arena.allocate(JAVA_CHAR);
		resultSegmt.set(JAVA_CHAR, 0, result);
		return resultSegmt;
	}

	public static MemorySegment createNewCharFromCharAndCharFromPtr_RetArgPtr(MemorySegment charArg1Addr, char charArg2) {
		MemorySegment charArg1Segmt = charArg1Addr.reinterpret(JAVA_CHAR.byteSize());
		char charArg1 = charArg1Segmt.get(JAVA_CHAR, 0);
		int diff = (charArg2 >= charArg1) ? (charArg2 - charArg1) : (charArg1 - charArg2);
		diff = (diff > 5) ? 5 : diff;
		char result = (char)(diff + 'A');
		charArg1Segmt.set(JAVA_CHAR, 0, result);
		return charArg1Segmt;
	}

	public static byte add2Bytes(byte byteArg1, byte byteArg2) {
		byte byteSum = (byte)(byteArg1 + byteArg2);
		return byteSum;
	}

	public static byte addByteAndByteFromPointer(byte byteArg1, MemorySegment byteArg2Addr) {
		MemorySegment byteArg2Segmt = byteArg2Addr.reinterpret(JAVA_BYTE.byteSize());
		byte byteArg2 = byteArg2Segmt.get(JAVA_BYTE, 0);
		byte byteSum = (byte)(byteArg1 + byteArg2);
		return byteSum;
	}

	public static MemorySegment addByteAndByteFromPtr_RetPtr(byte byteArg1, MemorySegment byteArg2Addr) {
		MemorySegment byteArg2Segmt = byteArg2Addr.reinterpret(JAVA_BYTE.byteSize());
		byte byteArg2 = byteArg2Segmt.get(JAVA_BYTE, 0);
		byte byteSum = (byte)(byteArg1 + byteArg2);
		MemorySegment resultSegmt = arena.allocate(JAVA_BYTE);
		resultSegmt.set(JAVA_BYTE, 0, byteSum);
		return resultSegmt;
	}

	public static MemorySegment addByteAndByteFromPtr_RetArgPtr(byte byteArg1, MemorySegment byteArg2Addr) {
		MemorySegment byteArg2Segmt = byteArg2Addr.reinterpret(JAVA_BYTE.byteSize());
		byte byteArg2 = byteArg2Segmt.get(JAVA_BYTE, 0);
		byte byteSum = (byte)(byteArg1 + byteArg2);
		byteArg2Segmt.set(JAVA_BYTE, 0, byteSum);
		return byteArg2Segmt;
	}

	public static short add2Shorts(short shortArg1, short shortArg2) {
		short shortSum = (short)(shortArg1 + shortArg2);
		return shortSum;
	}

	public static short addShortAndShortFromPointer(MemorySegment shortArg1Addr, short shortArg2) {
		MemorySegment shortArg1Segmt = shortArg1Addr.reinterpret(JAVA_SHORT.byteSize());
		short shortArg1 = shortArg1Segmt.get(JAVA_SHORT, 0);
		short shortSum = (short)(shortArg1 + shortArg2);
		return shortSum;
	}

	public static MemorySegment addShortAndShortFromPtr_RetPtr(MemorySegment shortArg1Addr, short shortArg2) {
		MemorySegment shortArg1Segmt = shortArg1Addr.reinterpret(JAVA_SHORT.byteSize());
		short shortArg1 = shortArg1Segmt.get(JAVA_SHORT, 0);
		short shortSum = (short)(shortArg1 + shortArg2);
		MemorySegment resultSegmt = arena.allocate(JAVA_SHORT);
		resultSegmt.set(JAVA_SHORT, 0, shortSum);
		return resultSegmt;
	}

	public static MemorySegment addShortAndShortFromPtr_RetArgPtr(MemorySegment shortArg1Addr, short shortArg2) {
		MemorySegment shortArg1Segmt = shortArg1Addr.reinterpret(JAVA_SHORT.byteSize());
		short shortArg1 = shortArg1Segmt.get(JAVA_SHORT, 0);
		short shortSum = (short)(shortArg1 + shortArg2);
		shortArg1Segmt.set(JAVA_SHORT, 0, shortSum);
		return shortArg1Segmt;
	}

	public static int add2Ints(int intArg1, int intArg2) {
		int intSum = intArg1 + intArg2;
		return intSum;
	}

	public static int addIntAndIntFromPointer(int intArg1, MemorySegment intArg2Addr) {
		MemorySegment intArg2Segmt = intArg2Addr.reinterpret(JAVA_INT.byteSize());
		int intArg2 = intArg2Segmt.get(JAVA_INT, 0);
		int intSum = intArg1 + intArg2;
		return intSum;
	}

	public static MemorySegment addIntAndIntFromPtr_RetPtr(int intArg1, MemorySegment intArg2Addr) {
		MemorySegment intArg2Segmt = intArg2Addr.reinterpret(JAVA_INT.byteSize());
		int intArg2 = intArg2Segmt.get(JAVA_INT, 0);
		int intSum = intArg1 + intArg2;
		MemorySegment resultSegmt = arena.allocate(JAVA_INT);
		resultSegmt.set(JAVA_INT, 0, intSum);
		return resultSegmt;
	}

	public static MemorySegment addIntAndIntFromPtr_RetArgPtr(int intArg1, MemorySegment intArg2Addr) {
		MemorySegment intArg2Segmt = intArg2Addr.reinterpret(JAVA_INT.byteSize());
		int intArg2 = intArg2Segmt.get(JAVA_INT, 0);
		int intSum = intArg1 + intArg2;
		intArg2Segmt.set(JAVA_INT, 0, intSum);
		return intArg2Segmt;
	}

	public static int add3Ints(int intArg1, int intArg2, int intArg3) {
		int intSum = intArg1 + intArg2 + intArg3;
		return intSum;
	}

	public static int addIntAndChar(int intArg, char charArg) {
		int sum = intArg + charArg;
		return sum;
	}

	public static void add2IntsReturnVoid(int intArg1, int intArg2) {
		int intSum = intArg1 + intArg2;
		System.out.println("add2IntsReturnVoid: intSum = " + intSum + "\n");
	}

	public static long add2Longs(long longArg1, long longArg2) {
		long longSum = longArg1 + longArg2;
		return longSum;
	}

	public static long addLongAndLongFromPointer(MemorySegment longArg1Addr, long longArg2) {
		MemorySegment longArg1Segmt = longArg1Addr.reinterpret(JAVA_LONG.byteSize());
		long longArg1 = longArg1Segmt.get(JAVA_LONG, 0);
		long longSum = longArg1 + longArg2;
		return longSum;
	}

	public static MemorySegment addLongAndLongFromPtr_RetPtr(MemorySegment longArg1Addr, long longArg2) {
		MemorySegment longArg1Segmt = longArg1Addr.reinterpret(JAVA_LONG.byteSize());
		long longArg1 = longArg1Segmt.get(JAVA_LONG, 0);
		long longSum = longArg1 + longArg2;
		MemorySegment resultSegmt = arena.allocate(JAVA_LONG);
		resultSegmt.set(JAVA_LONG, 0, longSum);
		return resultSegmt;
	}

	public static MemorySegment addLongAndLongFromPtr_RetArgPtr(MemorySegment longArg1Addr, long longArg2) {
		MemorySegment longArg1Segmt = longArg1Addr.reinterpret(JAVA_LONG.byteSize());
		long longArg1 = longArg1Segmt.get(JAVA_LONG, 0);
		long longSum = longArg1 + longArg2;
		longArg1Segmt.set(JAVA_LONG, 0, longSum);
		return longArg1Segmt;
	}

	public static float add2Floats(float floatArg1, float floatArg2) {
		float floatSum = floatArg1 + floatArg2;
		return floatSum;
	}

	public static float addFloatAndFloatFromPointer(float floatArg1, MemorySegment floatArg2Addr) {
		MemorySegment floatArg2Segmt = floatArg2Addr.reinterpret(JAVA_FLOAT.byteSize());
		float floatArg2 = floatArg2Segmt.get(JAVA_FLOAT, 0);
		float floatSum = floatArg1 + floatArg2;
		return floatSum;
	}

	public static MemorySegment addFloatAndFloatFromPtr_RetPtr(float floatArg1, MemorySegment floatArg2Addr) {
		MemorySegment floatArg2Segmt = floatArg2Addr.reinterpret(JAVA_FLOAT.byteSize());
		float floatArg2 = floatArg2Segmt.get(JAVA_FLOAT, 0);
		float floatSum = floatArg1 + floatArg2;
		MemorySegment resultSegmt = arena.allocate(JAVA_FLOAT);
		resultSegmt.set(JAVA_FLOAT, 0, floatSum);
		return resultSegmt;
	}

	public static MemorySegment addFloatAndFloatFromPtr_RetArgPtr(float floatArg1, MemorySegment floatArg2Addr) {
		MemorySegment floatArg2Segmt = floatArg2Addr.reinterpret(JAVA_FLOAT.byteSize());
		float floatArg2 = floatArg2Segmt.get(JAVA_FLOAT, 0);
		float floatSum = floatArg1 + floatArg2;
		floatArg2Segmt.set(JAVA_FLOAT, 0, floatSum);
		return floatArg2Segmt;
	}

	public static double add2Doubles(double doubleArg1, double doubleArg2) {
		double doubleSum = doubleArg1 + doubleArg2;
		return doubleSum;
	}

	public static double addDoubleAndDoubleFromPointer(MemorySegment doubleArg1Addr, double doubleArg2) {
		MemorySegment doubleArg1Segmt = doubleArg1Addr.reinterpret(JAVA_DOUBLE.byteSize());
		double doubleArg1 = doubleArg1Segmt.get(JAVA_DOUBLE, 0);
		double doubleSum = doubleArg1 + doubleArg2;
		return doubleSum;
	}

	public static MemorySegment addDoubleAndDoubleFromPtr_RetPtr(MemorySegment doubleArg1Addr, double doubleArg2) {
		MemorySegment doubleArg1Segmt = doubleArg1Addr.reinterpret(JAVA_DOUBLE.byteSize());
		double doubleArg1 = doubleArg1Segmt.get(JAVA_DOUBLE, 0);
		double doubleSum = doubleArg1 + doubleArg2;
		MemorySegment resultSegmt = arena.allocate(JAVA_DOUBLE);
		resultSegmt.set(JAVA_DOUBLE, 0, doubleSum);
		return resultSegmt;
	}

	public static MemorySegment addDoubleAndDoubleFromPtr_RetArgPtr(MemorySegment doubleArg1Addr, double doubleArg2) {
		MemorySegment doubleArg1Segmt = doubleArg1Addr.reinterpret(JAVA_DOUBLE.byteSize());
		double doubleArg1 = doubleArg1Segmt.get(JAVA_DOUBLE, 0);
		double doubleSum = doubleArg1 + doubleArg2;
		doubleArg1Segmt.set(JAVA_DOUBLE, 0, doubleSum);
		return doubleArg1Segmt;
	}

	public static int compare(MemorySegment argAddr1, MemorySegment argAddr2) {
		MemorySegment arg1Segmt = argAddr1.reinterpret(JAVA_INT.byteSize());
		MemorySegment arg2Segmt = argAddr2.reinterpret(JAVA_INT.byteSize());
		int intArg1 = arg1Segmt.get(JAVA_INT, 0);
		int intArg2 = arg2Segmt.get(JAVA_INT, 0);
		return (intArg1 - intArg2);
	}

	public static boolean addBoolAndBoolsFromStructWithXor(boolean arg1, MemorySegment arg2) {
		boolean boolSum = arg1 ^ arg2.get(JAVA_BOOLEAN, 0) ^ arg2.get(JAVA_BOOLEAN, 1);
		return boolSum;
	}

	public static boolean addBoolAnd20BoolsFromStructWithXor(boolean arg1, MemorySegment arg2) {
		boolean boolSum = arg1 ^ arg2.get(JAVA_BOOLEAN, 0) ^ arg2.get(JAVA_BOOLEAN, 1)
				^ arg2.get(JAVA_BOOLEAN, 2) ^ arg2.get(JAVA_BOOLEAN, 3) ^ arg2.get(JAVA_BOOLEAN, 4)
				^ arg2.get(JAVA_BOOLEAN, 5) ^ arg2.get(JAVA_BOOLEAN, 6) ^ arg2.get(JAVA_BOOLEAN, 7)
				^ arg2.get(JAVA_BOOLEAN, 8) ^ arg2.get(JAVA_BOOLEAN, 9) ^ arg2.get(JAVA_BOOLEAN, 10)
				^ arg2.get(JAVA_BOOLEAN, 11) ^ arg2.get(JAVA_BOOLEAN, 12) ^ arg2.get(JAVA_BOOLEAN, 13)
				^ arg2.get(JAVA_BOOLEAN, 14) ^ arg2.get(JAVA_BOOLEAN, 15) ^ arg2.get(JAVA_BOOLEAN, 16)
				^ arg2.get(JAVA_BOOLEAN, 17) ^ arg2.get(JAVA_BOOLEAN, 18) ^ arg2.get(JAVA_BOOLEAN, 19);
		return boolSum;
	}

	public static boolean addBoolFromPointerAndBoolsFromStructWithXor(MemorySegment arg1Addr, MemorySegment arg2) {
		MemorySegment arg1Segmt = arg1Addr.reinterpret(JAVA_BOOLEAN.byteSize());
		boolean boolSum = arg1Segmt.get(JAVA_BOOLEAN, 0) ^ arg2.get(JAVA_BOOLEAN, 0) ^ arg2.get(JAVA_BOOLEAN, 1);
		return boolSum;
	}

	public static MemorySegment addBoolFromPointerAndBoolsFromStructWithXor_returnBoolPointer(MemorySegment arg1Addr, MemorySegment arg2) {
		MemorySegment arg1Segmt = arg1Addr.reinterpret(JAVA_BOOLEAN.byteSize());
		boolean boolSum = arg1Segmt.get(JAVA_BOOLEAN, 0) ^ arg2.get(JAVA_BOOLEAN, 0) ^ arg2.get(JAVA_BOOLEAN, 1);
		arg1Segmt.set(JAVA_BOOLEAN, 0, boolSum);
		return arg1Addr;
	}

	public static boolean addBoolAndBoolsFromStructPointerWithXor(boolean arg1, MemorySegment arg2Addr) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_BOOLEAN.withName("elem1"), JAVA_BOOLEAN.withName("elem2"));
		MemorySegment arg2Segmt = arg2Addr.reinterpret(structLayout.byteSize());
		boolean boolSum = arg1 ^ arg2Segmt.get(JAVA_BOOLEAN, 0) ^ arg2Segmt.get(JAVA_BOOLEAN, 1);
		return boolSum;
	}

	public static boolean addBoolAndBoolsFromNestedStructWithXor(boolean arg1, MemorySegment arg2) {
		boolean nestedStructElem1 = arg2.get(JAVA_BOOLEAN, 0);
		boolean nestedStructElem2 = arg2.get(JAVA_BOOLEAN, 1);
		boolean structElem2 = arg2.get(JAVA_BOOLEAN, 2);
		boolean boolSum = arg1 ^ nestedStructElem1 ^ nestedStructElem2 ^ structElem2;
		return boolSum;
	}

	public static boolean addBoolAndBoolsFromNestedStructWithXor_reverseOrder(boolean arg1, MemorySegment arg2) {
		boolean structElem1 = arg2.get(JAVA_BOOLEAN, 0);
		boolean nestedStructElem1 = arg2.get(JAVA_BOOLEAN, 1);
		boolean nestedStructElem2 = arg2.get(JAVA_BOOLEAN, 2);
		boolean boolSum = arg1 ^ structElem1 ^ nestedStructElem1 ^ nestedStructElem2;
		return boolSum;
	}

	public static boolean addBoolAndBoolsFromStructWithNestedBoolArray(boolean arg1, MemorySegment arg2) {
		boolean nestedBoolArrayElem1 = arg2.get(JAVA_BOOLEAN, 0);
		boolean nestedBoolArrayElem2 = arg2.get(JAVA_BOOLEAN, 1);
		boolean structElem2 = arg2.get(JAVA_BOOLEAN, 2);

		boolean boolSum = arg1 ^ nestedBoolArrayElem1 ^ nestedBoolArrayElem2 ^ structElem2;
		return boolSum;
	}

	public static boolean addBoolAndBoolsFromStructWithNestedBoolArray_reverseOrder(boolean arg1, MemorySegment arg2) {
		boolean structElem1 = arg2.get(JAVA_BOOLEAN, 0);
		boolean nestedBoolArrayElem1 = arg2.get(JAVA_BOOLEAN, 1);
		boolean nestedBoolArrayElem2 = arg2.get(JAVA_BOOLEAN, 2);

		boolean boolSum = arg1 ^ structElem1 ^ nestedBoolArrayElem1 ^ nestedBoolArrayElem2;
		return boolSum;
	}

	public static boolean addBoolAndBoolsFromStructWithNestedStructArray(boolean arg1, MemorySegment arg2) {
		boolean nestedStructArrayElem1_Elem1 = arg2.get(JAVA_BOOLEAN, 0);
		boolean nestedStructArrayElem1_Elem2 = arg2.get(JAVA_BOOLEAN, 1);
		boolean nestedStructArrayElem2_Elem1 = arg2.get(JAVA_BOOLEAN, 2);
		boolean nestedStructArrayElem2_Elem2 = arg2.get(JAVA_BOOLEAN, 3);
		boolean structElem2 = arg2.get(JAVA_BOOLEAN, 4);

		boolean boolSum = arg1 ^ structElem2
				^ nestedStructArrayElem1_Elem1 ^ nestedStructArrayElem1_Elem2
				^ nestedStructArrayElem2_Elem1 ^ nestedStructArrayElem2_Elem2;
		return boolSum;
	}

	public static boolean addBoolAndBoolsFromStructWithNestedStructArray_reverseOrder(boolean arg1, MemorySegment arg2) {
		boolean structElem1 = arg2.get(JAVA_BOOLEAN, 0);
		boolean nestedStructArrayElem1_Elem1 = arg2.get(JAVA_BOOLEAN, 1);
		boolean nestedStructArrayElem1_Elem2 = arg2.get(JAVA_BOOLEAN, 2);
		boolean nestedStructArrayElem2_Elem1 = arg2.get(JAVA_BOOLEAN, 3);
		boolean nestedStructArrayElem2_Elem2 = arg2.get(JAVA_BOOLEAN, 4);

		boolean boolSum = arg1 ^ structElem1
				^ nestedStructArrayElem1_Elem1 ^ nestedStructArrayElem1_Elem2
				^ nestedStructArrayElem2_Elem1 ^ nestedStructArrayElem2_Elem2;
		return boolSum;
	}

	public static MemorySegment add2BoolStructsWithXor_returnStruct(MemorySegment arg1, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_BOOLEAN.withName("elem1"), JAVA_BOOLEAN.withName("elem2"));
		MemorySegment boolStructSegmt = arena.allocate(structLayout);
		boolean boolStruct_Elem1 = arg1.get(JAVA_BOOLEAN, 0) ^ arg2.get(JAVA_BOOLEAN, 0);
		boolean boolStruct_Elem2 = arg1.get(JAVA_BOOLEAN, 1) ^ arg2.get(JAVA_BOOLEAN, 1);
		boolStructSegmt.set(JAVA_BOOLEAN, 0, boolStruct_Elem1);
		boolStructSegmt.set(JAVA_BOOLEAN, 1, boolStruct_Elem2);
		return boolStructSegmt;
	}

	public static MemorySegment add2BoolStructsWithXor_returnStructPointer(MemorySegment arg1Addr, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_BOOLEAN.withName("elem1"), JAVA_BOOLEAN.withName("elem2"));
		MemorySegment arg1Segmt = arg1Addr.reinterpret(structLayout.byteSize());
		boolean boolStruct_Elem1 = arg1Segmt.get(JAVA_BOOLEAN, 0) ^ arg2.get(JAVA_BOOLEAN, 0);
		boolean boolStruct_Elem2 = arg1Segmt.get(JAVA_BOOLEAN, 1) ^ arg2.get(JAVA_BOOLEAN, 1);
		arg1Segmt.set(JAVA_BOOLEAN, 0, boolStruct_Elem1);
		arg1Segmt.set(JAVA_BOOLEAN, 1, boolStruct_Elem2);
		return arg1Addr;
	}

	public static MemorySegment add3BoolStructsWithXor_returnStruct(MemorySegment arg1, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_BOOLEAN.withName("elem1"),
				JAVA_BOOLEAN.withName("elem2"), JAVA_BOOLEAN.withName("elem3"));
		MemorySegment boolStructSegmt = arena.allocate(structLayout);
		boolean boolStruct_Elem1 = arg1.get(JAVA_BOOLEAN, 0) ^ arg2.get(JAVA_BOOLEAN, 0);
		boolean boolStruct_Elem2 = arg1.get(JAVA_BOOLEAN, 1) ^ arg2.get(JAVA_BOOLEAN, 1);
		boolean boolStruct_Elem3 = arg1.get(JAVA_BOOLEAN, 2) ^ arg2.get(JAVA_BOOLEAN, 2);
		boolStructSegmt.set(JAVA_BOOLEAN, 0, boolStruct_Elem1);
		boolStructSegmt.set(JAVA_BOOLEAN, 1, boolStruct_Elem2);
		boolStructSegmt.set(JAVA_BOOLEAN, 2, boolStruct_Elem3);
		return boolStructSegmt;
	}

	public static byte addByteAndBytesFromStruct(byte arg1, MemorySegment arg2) {
		byte byteSum = (byte)(arg1 + arg2.get(JAVA_BYTE, 0) + arg2.get(JAVA_BYTE, 1));
		return byteSum;
	}

	public static byte addByteAnd20BytesFromStruct(byte arg1, MemorySegment arg2) {
		byte byteSum = (byte)(arg1 + arg2.get(JAVA_BYTE, 0) + arg2.get(JAVA_BYTE, 1)
				+ arg2.get(JAVA_BYTE, 2) + arg2.get(JAVA_BYTE, 3) + arg2.get(JAVA_BYTE, 4)
				+ arg2.get(JAVA_BYTE, 5) + arg2.get(JAVA_BYTE, 6) + arg2.get(JAVA_BYTE, 7)
				+ arg2.get(JAVA_BYTE, 8) + arg2.get(JAVA_BYTE, 9) + arg2.get(JAVA_BYTE, 10)
				+ arg2.get(JAVA_BYTE, 11) + arg2.get(JAVA_BYTE, 12) + arg2.get(JAVA_BYTE, 13)
				+ arg2.get(JAVA_BYTE, 14) + arg2.get(JAVA_BYTE, 15) + arg2.get(JAVA_BYTE, 16)
				+ arg2.get(JAVA_BYTE, 17) + arg2.get(JAVA_BYTE, 18) + arg2.get(JAVA_BYTE, 19));
		return byteSum;
	}

	public static byte addByteFromPointerAndBytesFromStruct(MemorySegment arg1Addr, MemorySegment arg2) {
		MemorySegment arg1Segmt = arg1Addr.reinterpret(JAVA_BYTE.byteSize());
		byte byteSum = (byte)(arg1Segmt.get(JAVA_BYTE, 0) + arg2.get(JAVA_BYTE, 0) + arg2.get(JAVA_BYTE, 1));
		return byteSum;
	}

	public static MemorySegment addByteFromPointerAndBytesFromStruct_returnBytePointer(MemorySegment arg1Addr, MemorySegment arg2) {
		MemorySegment arg1Segmt = arg1Addr.reinterpret(JAVA_BYTE.byteSize());
		byte byteSum = (byte)(arg1Segmt.get(JAVA_BYTE, 0) + arg2.get(JAVA_BYTE, 0) + arg2.get(JAVA_BYTE, 1));
		arg1Segmt.set(JAVA_BYTE, 0, byteSum);
		return arg1Addr;
	}

	public static byte addByteAndBytesFromStructPointer(byte arg1, MemorySegment arg2Addr) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_BYTE.withName("elem1"), JAVA_BYTE.withName("elem2"));
		MemorySegment arg2Segmt = arg2Addr.reinterpret(structLayout.byteSize());
		byte byteSum = (byte)(arg1 + arg2Segmt.get(JAVA_BYTE, 0) + arg2Segmt.get(JAVA_BYTE, 1));
		return byteSum;
	}

	public static byte addByteAndBytesFromNestedStruct(byte arg1, MemorySegment arg2) {
		byte nestedStructElem1 = arg2.get(JAVA_BYTE, 0);
		byte nestedStructElem2 = arg2.get(JAVA_BYTE, 1);
		byte structElem2 = arg2.get(JAVA_BYTE, 2);

		byte byteSum = (byte)(arg1 + nestedStructElem1 + nestedStructElem2 + structElem2);
		return byteSum;
	}

	public static byte addByteAndBytesFromNestedStruct_reverseOrder(byte arg1, MemorySegment arg2) {
		byte structElem1 = arg2.get(JAVA_BYTE, 0);
		byte nestedStructElem1 = arg2.get(JAVA_BYTE, 1);
		byte nestedStructElem2 = arg2.get(JAVA_BYTE, 2);

		byte byteSum = (byte)(arg1 + structElem1 + nestedStructElem1 + nestedStructElem2);
		return byteSum;
	}

	public static byte addByteAndBytesFromStructWithNestedByteArray(byte arg1, MemorySegment arg2) {
		byte nestedByteArrayElem1 = arg2.get(JAVA_BYTE, 0);
		byte nestedByteArrayElem2 = arg2.get(JAVA_BYTE, 1);
		byte structElem2 = arg2.get(JAVA_BYTE, 2);

		byte byteSum = (byte)(arg1 + nestedByteArrayElem1 + nestedByteArrayElem2 + structElem2);
		return byteSum;
	}

	public static byte addByteAndBytesFromStructWithNestedByteArray_reverseOrder(byte arg1, MemorySegment arg2) {
		byte structElem1 = arg2.get(JAVA_BYTE, 0);
		byte nestedByteArrayElem1 = arg2.get(JAVA_BYTE, 1);
		byte nestedByteArrayElem2 = arg2.get(JAVA_BYTE, 2);

		byte byteSum = (byte)(arg1 + structElem1 + nestedByteArrayElem1 + nestedByteArrayElem2);
		return byteSum;
	}

	public static byte addByteAndBytesFromStructWithNestedStructArray(byte arg1, MemorySegment arg2) {
		byte nestedStructArrayElem1_Elem1 = arg2.get(JAVA_BYTE, 0);
		byte nestedStructArrayElem1_Elem2 = arg2.get(JAVA_BYTE, 1);
		byte nestedStructArrayElem2_Elem1 = arg2.get(JAVA_BYTE, 2);
		byte nestedStructArrayElem2_Elem2 = arg2.get(JAVA_BYTE, 3);
		byte structElem2 = arg2.get(JAVA_BYTE, 4);

		byte byteSum = (byte)(arg1 + structElem2
				+ nestedStructArrayElem1_Elem1 + nestedStructArrayElem1_Elem2
				+ nestedStructArrayElem2_Elem1 + nestedStructArrayElem2_Elem2);
		return byteSum;
	}

	public static byte addByteAndBytesFromStructWithNestedStructArray_reverseOrder(byte arg1, MemorySegment arg2) {
		byte structElem1 = arg2.get(JAVA_BYTE, 0);
		byte nestedStructArrayElem1_Elem1 = arg2.get(JAVA_BYTE, 1);
		byte nestedStructArrayElem1_Elem2 = arg2.get(JAVA_BYTE, 2);
		byte nestedStructArrayElem2_Elem1 = arg2.get(JAVA_BYTE, 3);
		byte nestedStructArrayElem2_Elem2 = arg2.get(JAVA_BYTE, 4);

		byte byteSum = (byte)(arg1 + structElem1
				+ nestedStructArrayElem1_Elem1 + nestedStructArrayElem1_Elem2
				+ nestedStructArrayElem2_Elem1 + nestedStructArrayElem2_Elem2);
		return byteSum;
	}

	public static MemorySegment add1ByteStructs_returnStruct(MemorySegment arg1, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_BYTE.withName("elem1"));
		MemorySegment byteStructSegmt = arena.allocate(structLayout);
		byte byteStruct_Elem1 = (byte)(arg1.get(JAVA_BYTE, 0) + arg2.get(JAVA_BYTE, 0));
		byteStructSegmt.set(JAVA_BYTE, 0, byteStruct_Elem1);
		return byteStructSegmt;
	}

	public static MemorySegment add2ByteStructs_returnStruct(MemorySegment arg1, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_BYTE.withName("elem1"), JAVA_BYTE.withName("elem2"));
		MemorySegment byteStructSegmt = arena.allocate(structLayout);
		byte byteStruct_Elem1 = (byte)(arg1.get(JAVA_BYTE, 0) + arg2.get(JAVA_BYTE, 0));
		byte byteStruct_Elem2 = (byte)(arg1.get(JAVA_BYTE, 1) + arg2.get(JAVA_BYTE, 1));
		byteStructSegmt.set(JAVA_BYTE, 0, byteStruct_Elem1);
		byteStructSegmt.set(JAVA_BYTE, 1, byteStruct_Elem2);
		return byteStructSegmt;
	}

	public static MemorySegment add2ByteStructs_returnStructPointer(MemorySegment arg1Addr, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_BYTE.withName("elem1"), JAVA_BYTE.withName("elem2"));
		MemorySegment arg1Segmt = arg1Addr.reinterpret(structLayout.byteSize());
		byte byteStruct_Elem1 = (byte)(arg1Segmt.get(JAVA_BYTE, 0) + arg2.get(JAVA_BYTE, 0));
		byte byteStruct_Elem2 = (byte)(arg1Segmt.get(JAVA_BYTE, 1) + arg2.get(JAVA_BYTE, 1));
		arg1Segmt.set(JAVA_BYTE, 0, byteStruct_Elem1);
		arg1Segmt.set(JAVA_BYTE, 1, byteStruct_Elem2);
		return arg1Addr;
	}

	public static MemorySegment add3ByteStructs_returnStruct(MemorySegment arg1, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_BYTE.withName("elem1"),
				JAVA_BYTE.withName("elem2"), JAVA_BYTE.withName("elem3"));
		MemorySegment byteStructSegmt = arena.allocate(structLayout);
		byte byteStruct_Elem1 = (byte)(arg1.get(JAVA_BYTE, 0) + arg2.get(JAVA_BYTE, 0));
		byte byteStruct_Elem2 = (byte)(arg1.get(JAVA_BYTE, 1) + arg2.get(JAVA_BYTE, 1));
		byte byteStruct_Elem3 = (byte)(arg1.get(JAVA_BYTE, 2) + arg2.get(JAVA_BYTE, 2));
		byteStructSegmt.set(JAVA_BYTE, 0, byteStruct_Elem1);
		byteStructSegmt.set(JAVA_BYTE, 1, byteStruct_Elem2);
		byteStructSegmt.set(JAVA_BYTE, 2, byteStruct_Elem3);
		return byteStructSegmt;
	}

	public static char addCharAndCharsFromStruct(char arg1, MemorySegment arg2) {
		char result = (char)(arg1 + arg2.get(JAVA_CHAR, 0) + arg2.get(JAVA_CHAR, 2) - 2 * 'A');
		return result;
	}

	public static char addCharAnd10CharsFromStruct(char arg1, MemorySegment arg2) {
		char result = (char)(arg1 + arg2.get(JAVA_CHAR, 0) + arg2.get(JAVA_CHAR, 2)
		+ arg2.get(JAVA_CHAR, 4) + arg2.get(JAVA_CHAR, 6) + arg2.get(JAVA_CHAR, 8)
		+ arg2.get(JAVA_CHAR, 10) + arg2.get(JAVA_CHAR, 12) + arg2.get(JAVA_CHAR, 14)
		+ arg2.get(JAVA_CHAR, 16) + arg2.get(JAVA_CHAR, 18) - 10 * 'A');
		return result;
	}

	public static char addCharFromPointerAndCharsFromStruct(MemorySegment arg1Addr, MemorySegment arg2) {
		MemorySegment arg1Segmt = arg1Addr.reinterpret(JAVA_CHAR.byteSize());
		char result = (char)(arg1Segmt.get(JAVA_CHAR, 0) + arg2.get(JAVA_CHAR, 0) + arg2.get(JAVA_CHAR, 2) - 2 * 'A');
		return result;
	}

	public static MemorySegment addCharFromPointerAndCharsFromStruct_returnCharPointer(MemorySegment arg1Addr, MemorySegment arg2) {
		MemorySegment arg1Segmt = arg1Addr.reinterpret(JAVA_CHAR.byteSize());
		char result = (char)(arg1Segmt.get(JAVA_CHAR, 0) + arg2.get(JAVA_CHAR, 0) + arg2.get(JAVA_CHAR, 2) - 2 * 'A');
		arg1Segmt.set(JAVA_CHAR, 0, result);
		return arg1Addr;
	}

	public static char addCharAndCharsFromStructPointer(char arg1, MemorySegment arg2Addr) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_CHAR.withName("elem1"), JAVA_CHAR.withName("elem2"));
		MemorySegment arg2Segmt = arg2Addr.reinterpret(structLayout.byteSize());
		char result = (char)(arg1 + arg2Segmt.get(JAVA_CHAR, 0) + arg2Segmt.get(JAVA_CHAR, 2) - 2 * 'A');
		return result;
	}

	public static char addCharAndCharsFromNestedStruct(char arg1, MemorySegment arg2) {
		char nestedStructElem1 = arg2.get(JAVA_CHAR, 0);
		char nestedStructElem2 = arg2.get(JAVA_CHAR, 2);
		char structElem2 = arg2.get(JAVA_CHAR, 4);

		char result = (char)(arg1 + nestedStructElem1 + nestedStructElem2 + structElem2 - 3 * 'A');
		return result;
	}

	public static char addCharAndCharsFromNestedStruct_reverseOrder(char arg1, MemorySegment arg2) {
		char structElem1 = arg2.get(JAVA_CHAR, 0);
		char nestedStructElem1 = arg2.get(JAVA_CHAR, 2);
		char nestedStructElem2 = arg2.get(JAVA_CHAR, 4);

		char result = (char)(arg1 + structElem1 + nestedStructElem1 + nestedStructElem2 - 3 * 'A');
		return result;
	}

	public static char addCharAndCharsFromStructWithNestedCharArray(char arg1, MemorySegment arg2) {
		char nestedCharArrayElem1 = arg2.get(JAVA_CHAR, 0);
		char nestedCharArrayElem2 = arg2.get(JAVA_CHAR, 2);
		char structElem2 = arg2.get(JAVA_CHAR, 4);

		char result = (char)(arg1 + nestedCharArrayElem1 + nestedCharArrayElem2 + structElem2 - 3 * 'A');
		return result;
	}

	public static char addCharAndCharsFromStructWithNestedCharArray_reverseOrder(char arg1, MemorySegment arg2) {
		char structElem1 = arg2.get(JAVA_CHAR, 0);
		char nestedCharArrayElem1 = arg2.get(JAVA_CHAR, 2);
		char nestedCharArrayElem2 = arg2.get(JAVA_CHAR, 4);

		char result = (char)(arg1 + structElem1 + nestedCharArrayElem1 + nestedCharArrayElem2 - 3 * 'A');
		return result;
	}

	public static char addCharAndCharsFromStructWithNestedStructArray(char arg1, MemorySegment arg2) {
		char nestedStructArrayElem1_Elem1 = arg2.get(JAVA_CHAR, 0);
		char nestedStructArrayElem1_Elem2 = arg2.get(JAVA_CHAR, 2);
		char nestedStructArrayElem2_Elem1 = arg2.get(JAVA_CHAR, 4);
		char nestedStructArrayElem2_Elem2 = arg2.get(JAVA_CHAR, 6);
		char structElem2 = arg2.get(JAVA_CHAR, 8);

		char result = (char)(arg1 + structElem2
				+ nestedStructArrayElem1_Elem1 + nestedStructArrayElem1_Elem2
				+ nestedStructArrayElem2_Elem1 + nestedStructArrayElem2_Elem2 - 5 * 'A');
		return result;
	}

	public static char addCharAndCharsFromStructWithNestedStructArray_reverseOrder(char arg1, MemorySegment arg2) {
		char structElem1 = arg2.get(JAVA_CHAR, 0);
		char nestedStructArrayElem1_Elem1 = arg2.get(JAVA_CHAR, 2);
		char nestedStructArrayElem1_Elem2 = arg2.get(JAVA_CHAR, 4);
		char nestedStructArrayElem2_Elem1 = arg2.get(JAVA_CHAR, 6);
		char nestedStructArrayElem2_Elem2 = arg2.get(JAVA_CHAR, 8);

		char result = (char)(arg1 + structElem1
				+ nestedStructArrayElem1_Elem1 + nestedStructArrayElem1_Elem2
				+ nestedStructArrayElem2_Elem1 + nestedStructArrayElem2_Elem2 - 5 * 'A');
		return result;
	}

	public static MemorySegment add2CharStructs_returnStruct(MemorySegment arg1, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_CHAR.withName("elem1"), JAVA_CHAR.withName("elem2"));
		MemorySegment charStructSegmt = arena.allocate(structLayout);
		char charStruct_Elem1 = (char)(arg1.get(JAVA_CHAR, 0) + arg2.get(JAVA_CHAR, 0) - 'A');
		char charStruct_Elem2 = (char)(arg1.get(JAVA_CHAR, 2) + arg2.get(JAVA_CHAR, 2) - 'A');
		charStructSegmt.set(JAVA_CHAR, 0, charStruct_Elem1);
		charStructSegmt.set(JAVA_CHAR, 2, charStruct_Elem2);
		return charStructSegmt;
	}

	public static MemorySegment add2CharStructs_returnStructPointer(MemorySegment arg1Addr, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_CHAR.withName("elem1"), JAVA_CHAR.withName("elem2"));
		MemorySegment arg1Segmt = arg1Addr.reinterpret(structLayout.byteSize());
		char charStruct_Elem1 = (char)(arg1Segmt.get(JAVA_CHAR, 0) + arg2.get(JAVA_CHAR, 0) - 'A');
		char charStruct_Elem2 = (char)(arg1Segmt.get(JAVA_CHAR, 2) + arg2.get(JAVA_CHAR, 2) - 'A');
		arg1Segmt.set(JAVA_CHAR, 0, charStruct_Elem1);
		arg1Segmt.set(JAVA_CHAR, 2, charStruct_Elem2);
		return arg1Addr;
	}

	public static MemorySegment add3CharStructs_returnStruct(MemorySegment arg1, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_CHAR.withName("elem1"),
				JAVA_CHAR.withName("elem2"), JAVA_CHAR.withName("elem3"));
		MemorySegment charStructSegmt = arena.allocate(structLayout);
		char charStruct_Elem1 = (char)(arg1.get(JAVA_CHAR, 0) + arg2.get(JAVA_CHAR, 0) - 'A');
		char charStruct_Elem2 = (char)(arg1.get(JAVA_CHAR, 2) + arg2.get(JAVA_CHAR, 2) - 'A');
		char charStruct_Elem3 = (char)(arg1.get(JAVA_CHAR, 4) + arg2.get(JAVA_CHAR, 4) - 'A');
		charStructSegmt.set(JAVA_CHAR, 0, charStruct_Elem1);
		charStructSegmt.set(JAVA_CHAR, 2, charStruct_Elem2);
		charStructSegmt.set(JAVA_CHAR, 4, charStruct_Elem3);
		return charStructSegmt;
	}

	public static short addShortAndShortsFromStruct(short arg1, MemorySegment arg2) {
		short shortSum = (short)(arg1 + arg2.get(JAVA_SHORT, 0) + arg2.get(JAVA_SHORT, 2));
		return shortSum;
	}

	public static short addShortAnd10ShortsFromStruct(short arg1, MemorySegment arg2) {
		short shortSum = (short)(arg1 + arg2.get(JAVA_SHORT, 0) + arg2.get(JAVA_SHORT, 2)
		+ arg2.get(JAVA_SHORT, 4) + arg2.get(JAVA_SHORT, 6) + arg2.get(JAVA_SHORT, 8)
		+ arg2.get(JAVA_SHORT, 10) + arg2.get(JAVA_SHORT, 12) + arg2.get(JAVA_SHORT, 14)
		+ arg2.get(JAVA_SHORT, 16) + arg2.get(JAVA_SHORT, 18));
		return shortSum;
	}

	public static short addShortFromPointerAndShortsFromStruct(MemorySegment arg1Addr, MemorySegment arg2) {
		MemorySegment arg1Segmt = arg1Addr.reinterpret(JAVA_SHORT.byteSize());
		short shortSum = (short)(arg1Segmt.get(JAVA_SHORT, 0) + arg2.get(JAVA_SHORT, 0) + arg2.get(JAVA_SHORT, 2));
		return shortSum;
	}

	public static MemorySegment addShortFromPointerAndShortsFromStruct_returnShortPointer(MemorySegment arg1Addr, MemorySegment arg2) {
		MemorySegment arg1Segmt = arg1Addr.reinterpret(JAVA_SHORT.byteSize());
		short shortSum = (short)(arg1Segmt.get(JAVA_SHORT, 0) + arg2.get(JAVA_SHORT, 0) + arg2.get(JAVA_SHORT, 2));
		arg1Segmt.set(JAVA_SHORT, 0, shortSum);
		return arg1Addr;
	}

	public static short addShortAndShortsFromStructPointer(short arg1, MemorySegment arg2Addr) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_SHORT.withName("elem1"), JAVA_SHORT.withName("elem2"));
		MemorySegment arg2Segmt = arg2Addr.reinterpret(structLayout.byteSize());
		short shortSum = (short)(arg1 + arg2Segmt.get(JAVA_SHORT, 0) + arg2Segmt.get(JAVA_SHORT, 2));
		return shortSum;
	}

	public static short addShortAndShortsFromNestedStruct(short arg1, MemorySegment arg2) {
		short nestedStructElem1 = arg2.get(JAVA_SHORT, 0);
		short nestedStructElem2 = arg2.get(JAVA_SHORT, 2);
		short structElem2 = arg2.get(JAVA_SHORT, 4);

		short shortSum = (short)(arg1 + nestedStructElem1 + nestedStructElem2 + structElem2);
		return shortSum;
	}

	public static short addShortAndShortsFromNestedStruct_reverseOrder(short arg1, MemorySegment arg2) {
		short structElem1 = arg2.get(JAVA_SHORT, 0);
		short nestedStructElem1 = arg2.get(JAVA_SHORT, 2);
		short nestedStructElem2 = arg2.get(JAVA_SHORT, 4);

		short shortSum = (short)(arg1 + structElem1 + nestedStructElem1 + nestedStructElem2);
		return shortSum;
	}

	public static short addShortAndShortsFromStructWithNestedShortArray(short arg1, MemorySegment arg2) {
		short nestedShortArrayElem1 = arg2.get(JAVA_SHORT, 0);
		short nestedShortArrayElem2 = arg2.get(JAVA_SHORT, 2);
		short structElem2 = arg2.get(JAVA_SHORT, 4);

		short shortSum = (short)(arg1 + nestedShortArrayElem1 + nestedShortArrayElem2 + structElem2);
		return shortSum;
	}

	public static short addShortAndShortsFromStructWithNestedShortArray_reverseOrder(short arg1, MemorySegment arg2) {
		short structElem1 = arg2.get(JAVA_SHORT, 0);
		short nestedShortArrayElem1 = arg2.get(JAVA_SHORT, 2);
		short nestedShortArrayElem2 = arg2.get(JAVA_SHORT, 4);

		short shortSum = (short)(arg1 + structElem1 + nestedShortArrayElem1 + nestedShortArrayElem2);
		return shortSum;
	}

	public static short addShortAndShortsFromStructWithNestedStructArray(short arg1, MemorySegment arg2) {
		short nestedStructArrayElem1_Elem1 = arg2.get(JAVA_SHORT, 0);
		short nestedStructArrayElem1_Elem2 = arg2.get(JAVA_SHORT, 2);
		short nestedStructArrayElem2_Elem1 = arg2.get(JAVA_SHORT, 4);
		short nestedStructArrayElem2_Elem2 = arg2.get(JAVA_SHORT, 6);
		short structElem2 = arg2.get(JAVA_SHORT, 8);

		short shortSum = (short)(arg1 + structElem2
				+ nestedStructArrayElem1_Elem1 + nestedStructArrayElem1_Elem2
				+ nestedStructArrayElem2_Elem1 + nestedStructArrayElem2_Elem2);
		return shortSum;
	}

	public static short addShortAndShortsFromStructWithNestedStructArray_reverseOrder(short arg1, MemorySegment arg2) {
		short structElem1 = arg2.get(JAVA_SHORT, 0);
		short nestedStructArrayElem1_Elem1 = arg2.get(JAVA_SHORT, 2);
		short nestedStructArrayElem1_Elem2 = arg2.get(JAVA_SHORT, 4);
		short nestedStructArrayElem2_Elem1 = arg2.get(JAVA_SHORT, 6);
		short nestedStructArrayElem2_Elem2 = arg2.get(JAVA_SHORT, 8);

		short shortSum = (short)(arg1 + structElem1
				+ nestedStructArrayElem1_Elem1 + nestedStructArrayElem1_Elem2
				+ nestedStructArrayElem2_Elem1 + nestedStructArrayElem2_Elem2);
		return shortSum;
	}

	public static MemorySegment add2ShortStructs_returnStruct(MemorySegment arg1, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_SHORT.withName("elem1"), JAVA_SHORT.withName("elem2"));
		MemorySegment shortStructSegmt = arena.allocate(structLayout);
		short shortStruct_Elem1 = (short)(arg1.get(JAVA_SHORT, 0) + arg2.get(JAVA_SHORT, 0));
		short shortStruct_Elem2 = (short)(arg1.get(JAVA_SHORT, 2) + arg2.get(JAVA_SHORT, 2));
		shortStructSegmt.set(JAVA_SHORT, 0, shortStruct_Elem1);
		shortStructSegmt.set(JAVA_SHORT, 2, shortStruct_Elem2);
		return shortStructSegmt;
	}

	public static MemorySegment add2ShortStructs_returnStructPointer(MemorySegment arg1Addr, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_SHORT.withName("elem1"), JAVA_SHORT.withName("elem2"));
		MemorySegment arg1Segmt = arg1Addr.reinterpret(structLayout.byteSize());
		short shortStruct_Elem1 = (short)(arg1Segmt.get(JAVA_SHORT, 0) + arg2.get(JAVA_SHORT, 0));
		short shortStruct_Elem2 = (short)(arg1Segmt.get(JAVA_SHORT, 2) + arg2.get(JAVA_SHORT, 2));
		arg1Segmt.set(JAVA_SHORT, 0, shortStruct_Elem1);
		arg1Segmt.set(JAVA_SHORT, 2, shortStruct_Elem2);
		return arg1Addr;
	}

	public static MemorySegment add3ShortStructs_returnStruct(MemorySegment arg1, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_SHORT.withName("elem1"),
				JAVA_SHORT.withName("elem2"), JAVA_SHORT.withName("elem3"));
		MemorySegment shortStructSegmt = arena.allocate(structLayout);
		short shortStruct_Elem1 = (short)(arg1.get(JAVA_SHORT, 0) + arg2.get(JAVA_SHORT, 0));
		short shortStruct_Elem2 = (short)(arg1.get(JAVA_SHORT, 2) + arg2.get(JAVA_SHORT, 2));
		short shortStruct_Elem3 = (short)(arg1.get(JAVA_SHORT, 4) + arg2.get(JAVA_SHORT, 4));
		shortStructSegmt.set(JAVA_SHORT, 0, shortStruct_Elem1);
		shortStructSegmt.set(JAVA_SHORT, 2, shortStruct_Elem2);
		shortStructSegmt.set(JAVA_SHORT, 4, shortStruct_Elem3);
		return shortStructSegmt;
	}

	public static int addIntAndIntsFromStruct(int arg1, MemorySegment arg2) {
		int intSum = arg1 + arg2.get(JAVA_INT, 0) + arg2.get(JAVA_INT, 4);
		return intSum;
	}

	public static int addIntAnd5IntsFromStruct(int arg1, MemorySegment arg2) {
		int intSum = arg1 + arg2.get(JAVA_INT, 0) + arg2.get(JAVA_INT, 4)
		+ arg2.get(JAVA_INT, 8) + arg2.get(JAVA_INT, 12) + arg2.get(JAVA_INT, 16);
		return intSum;
	}

	public static int addIntFromPointerAndIntsFromStruct(MemorySegment arg1Addr, MemorySegment arg2) {
		MemorySegment arg1Segmt = arg1Addr.reinterpret(JAVA_INT.byteSize());
		int intSum = arg1Segmt.get(JAVA_INT, 0) + arg2.get(JAVA_INT, 0) + arg2.get(JAVA_INT, 4);
		return intSum;
	}

	public static MemorySegment addIntFromPointerAndIntsFromStruct_returnIntPointer(MemorySegment arg1Addr, MemorySegment arg2) {
		MemorySegment arg1Segmt = arg1Addr.reinterpret(JAVA_INT.byteSize());
		int intSum = arg1Segmt.get(JAVA_INT, 0) + arg2.get(JAVA_INT, 0) + arg2.get(JAVA_INT, 4);
		arg1Segmt.set(JAVA_INT, 0, intSum);
		return arg1Addr;
	}

	public static int addIntAndIntsFromStructPointer(int arg1, MemorySegment arg2Addr) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_INT.withName("elem1"), JAVA_INT.withName("elem2"));
		MemorySegment arg2Segmt = arg2Addr.reinterpret(structLayout.byteSize());
		int intSum = arg1 + arg2Segmt.get(JAVA_INT, 0) + arg2Segmt.get(JAVA_INT, 4);
		return intSum;
	}

	public static int addIntAndIntsFromNestedStruct(int arg1, MemorySegment arg2) {
		int nestedStructElem1 = arg2.get(JAVA_INT, 0);
		int nestedStructElem2 = arg2.get(JAVA_INT, 4);
		int structElem2 = arg2.get(JAVA_INT, 8);

		int intSum = arg1 + nestedStructElem1 + nestedStructElem2 + structElem2;
		return intSum;
	}

	public static int addIntAndIntsFromNestedStruct_reverseOrder(int arg1, MemorySegment arg2) {
		int structElem1 = arg2.get(JAVA_INT, 0);
		int nestedStructElem1 = arg2.get(JAVA_INT, 4);
		int nestedStructElem2 = arg2.get(JAVA_INT, 8);

		int intSum = arg1 + structElem1 + nestedStructElem1 + nestedStructElem2;
		return intSum;
	}

	public static int addIntAndIntsFromStructWithNestedIntArray(int arg1, MemorySegment arg2) {
		int nestedIntArrayElem1 = arg2.get(JAVA_INT, 0);
		int nestedIntArrayElem2 = arg2.get(JAVA_INT, 4);
		int structElem2 = arg2.get(JAVA_INT, 8);

		int intSum = arg1 + nestedIntArrayElem1 + nestedIntArrayElem2 + structElem2;
		return intSum;
	}

	public static int addIntAndIntsFromStructWithNestedIntArray_reverseOrder(int arg1, MemorySegment arg2) {
		int structElem1 = arg2.get(JAVA_INT, 0);
		int nestedIntArrayElem1 = arg2.get(JAVA_INT, 4);
		int nestedIntArrayElem2 = arg2.get(JAVA_INT, 8);

		int intSum = arg1 + structElem1 + nestedIntArrayElem1 + nestedIntArrayElem2;
		return intSum;
	}

	public static int addIntAndIntsFromStructWithNestedStructArray(int arg1, MemorySegment arg2) {
		int nestedStructArrayElem1_Elem1 = arg2.get(JAVA_INT, 0);
		int nestedStructArrayElem1_Elem2 = arg2.get(JAVA_INT, 4);
		int nestedStructArrayElem2_Elem1 = arg2.get(JAVA_INT, 8);
		int nestedStructArrayElem2_Elem2 = arg2.get(JAVA_INT, 12);
		int structElem2 = arg2.get(JAVA_INT, 16);

		int intSum = arg1 + structElem2
				+ nestedStructArrayElem1_Elem1 + nestedStructArrayElem1_Elem2
				+ nestedStructArrayElem2_Elem1 + nestedStructArrayElem2_Elem2;
		return intSum;
	}

	public static int addIntAndIntsFromStructWithNestedStructArray_reverseOrder(int arg1, MemorySegment arg2) {
		int structElem1 = arg2.get(JAVA_INT, 0);
		int nestedStructArrayElem1_Elem1 = arg2.get(JAVA_INT, 4);
		int nestedStructArrayElem1_Elem2 = arg2.get(JAVA_INT, 8);
		int nestedStructArrayElem2_Elem1 = arg2.get(JAVA_INT, 12);
		int nestedStructArrayElem2_Elem2 = arg2.get(JAVA_INT, 16);

		int intSum = arg1 + structElem1
				+ nestedStructArrayElem1_Elem1 + nestedStructArrayElem1_Elem2
				+ nestedStructArrayElem2_Elem1 + nestedStructArrayElem2_Elem2;
		return intSum;
	}

	public static MemorySegment add2IntStructs_returnStruct(MemorySegment arg1, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_INT.withName("elem1"), JAVA_INT.withName("elem2"));
		MemorySegment intStructSegmt = arena.allocate(structLayout);
		int intStruct_Elem1 = arg1.get(JAVA_INT, 0) + arg2.get(JAVA_INT, 0);
		int intStruct_Elem2 = arg1.get(JAVA_INT, 4) + arg2.get(JAVA_INT, 4);
		intStructSegmt.set(JAVA_INT, 0, intStruct_Elem1);
		intStructSegmt.set(JAVA_INT, 4, intStruct_Elem2);
		return intStructSegmt;
	}

	public static MemorySegment add2IntStructs_returnStruct_throwException(MemorySegment arg1, MemorySegment arg2) {
		throw new IllegalArgumentException("An exception is thrown from the upcall method");
	}

	public static MemorySegment add2IntStructs_returnStruct_nestedUpcall(MemorySegment arg1, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_INT.withName("elem1"), JAVA_INT.withName("elem2"));
		FunctionDescriptor fd = FunctionDescriptor.of(structLayout, structLayout, structLayout, ADDRESS);
		MemorySegment functionSymbol = nativeLibLookup.find("add2IntStructs_returnStructByUpcallMH").get();
		MethodHandle mh = linker.downcallHandle(functionSymbol, fd);
		MemorySegment upcallFuncAddr = linker.upcallStub(UpcallMethodHandles.MH_add2IntStructs_returnStruct_throwException,
				FunctionDescriptor.of(structLayout, structLayout, structLayout), arena);
		MemorySegment resultSegmt = null;
		try {
			resultSegmt = (MemorySegment)mh.invoke((SegmentAllocator)arena, arg1, arg2, upcallFuncAddr);
		} catch (Throwable e) {
			throw (IllegalArgumentException)e;
		}
		return resultSegmt;
	}

	public static MemorySegment add2IntStructs_returnStruct_nullValue(MemorySegment arg1, MemorySegment arg2) {
		return null;
	}

	public static MemorySegment add2IntStructs_returnStruct_nullSegmt(MemorySegment arg1, MemorySegment arg2) {
		return MemorySegment.NULL;
	}

	public static MemorySegment add2IntStructs_returnStruct_heapSegmt(MemorySegment arg1, MemorySegment arg2) {
		int intStruct_Elem1 = arg1.get(JAVA_INT, 0) + arg2.get(JAVA_INT, 0);
		int intStruct_Elem2 = arg1.get(JAVA_INT, 4) + arg2.get(JAVA_INT, 4);
		return MemorySegment.ofArray(new int[]{intStruct_Elem1, intStruct_Elem2});
	}

	public static MemorySegment add2IntStructs_returnStructPointer(MemorySegment arg1Addr, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_INT.withName("elem1"), JAVA_INT.withName("elem2"));
		MemorySegment arg1Segmt = arg1Addr.reinterpret(structLayout.byteSize());
		int intSum_Elem1 = arg1Segmt.get(JAVA_INT, 0) + arg2.get(JAVA_INT, 0);
		int intSum_Elem2 = arg1Segmt.get(JAVA_INT, 4) + arg2.get(JAVA_INT, 4);
		arg1Segmt.set(JAVA_INT, 0, intSum_Elem1);
		arg1Segmt.set(JAVA_INT, 4, intSum_Elem2);
		return arg1Addr;
	}

	public static MemorySegment add2IntStructs_returnStructPointer_nullValue(MemorySegment arg1Addr, MemorySegment arg2) {
		return null;
	}

	public static MemorySegment add2IntStructs_returnStructPointer_nullSegmt(MemorySegment arg1Addr, MemorySegment arg2) {
		return MemorySegment.NULL;
	}

	public static MemorySegment add2IntStructs_returnStructPointer_heapSegmt(MemorySegment arg1Addr, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_INT.withName("elem1"), JAVA_INT.withName("elem2"));
		MemorySegment arg1Segmt = arg1Addr.reinterpret(structLayout.byteSize());
		int intSum_Elem1 = arg1Segmt.get(JAVA_INT, 0) + arg2.get(JAVA_INT, 0);
		int intSum_Elem2 = arg1Segmt.get(JAVA_INT, 4) + arg2.get(JAVA_INT, 4);
		return MemorySegment.ofArray(new int[]{intSum_Elem1, intSum_Elem2});
	}

	public static MemorySegment add3IntStructs_returnStruct(MemorySegment arg1, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_INT.withName("elem1"), JAVA_INT.withName("elem2"), JAVA_INT.withName("elem3"));
		MemorySegment intStructSegmt = arena.allocate(structLayout);
		int intStruct_Elem1 = arg1.get(JAVA_INT, 0) + arg2.get(JAVA_INT, 0);
		int intStruct_Elem2 = arg1.get(JAVA_INT, 4) + arg2.get(JAVA_INT, 4);
		int intStruct_Elem3 = arg1.get(JAVA_INT, 8) + arg2.get(JAVA_INT, 8);
		intStructSegmt.set(JAVA_INT, 0, intStruct_Elem1);
		intStructSegmt.set(JAVA_INT, 4, intStruct_Elem2);
		intStructSegmt.set(JAVA_INT, 8, intStruct_Elem3);
		return intStructSegmt;
	}

	public static long addLongAndLongsFromStruct(long arg1, MemorySegment arg2) {
		long longSum = arg1 + arg2.get(JAVA_LONG, 0) + arg2.get(JAVA_LONG, 8);
		return longSum;
	}

	public static long addLongFromPointerAndLongsFromStruct(MemorySegment arg1Addr, MemorySegment arg2) {
		MemorySegment arg1Segmt = arg1Addr.reinterpret(JAVA_LONG.byteSize());
		long longSum = arg1Segmt.get(JAVA_LONG, 0) + arg2.get(JAVA_LONG, 0) + arg2.get(JAVA_LONG, 8);
		return longSum;
	}

	public static MemorySegment addLongFromPointerAndLongsFromStruct_returnLongPointer(MemorySegment arg1Addr, MemorySegment arg2) {
		MemorySegment arg1Segmt = arg1Addr.reinterpret(JAVA_LONG.byteSize());
		long longSum = arg1Segmt.get(JAVA_LONG, 0) + arg2.get(JAVA_LONG, 0) + arg2.get(JAVA_LONG, 8);
		arg1Segmt.set(JAVA_LONG, 0, longSum);
		return arg1Addr;
	}

	public static long addLongAndLongsFromStructPointer(long arg1, MemorySegment arg2Addr) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_LONG.withName("elem1"), JAVA_LONG.withName("elem2"));
		MemorySegment arg2Segmt = arg2Addr.reinterpret(structLayout.byteSize());
		long longSum = arg1 + arg2Segmt.get(JAVA_LONG, 0) + arg2Segmt.get(JAVA_LONG, 8);
		return longSum;
	}

	public static long addLongAndLongsFromNestedStruct(long arg1, MemorySegment arg2) {
		long nestedStructElem1 = arg2.get(JAVA_LONG, 0);
		long nestedStructElem2 = arg2.get(JAVA_LONG, 8);
		long structElem2 = arg2.get(JAVA_LONG, 16);

		long longSum = arg1 + nestedStructElem1 + nestedStructElem2 + structElem2;
		return longSum;
	}

	public static long addLongAndLongsFromNestedStruct_reverseOrder(long arg1, MemorySegment arg2) {
		long structElem1 = arg2.get(JAVA_LONG, 0);
		long nestedStructElem1 = arg2.get(JAVA_LONG, 8);
		long nestedStructElem2 = arg2.get(JAVA_LONG, 16);

		long longSum = arg1 + structElem1 + nestedStructElem1 + nestedStructElem2;
		return longSum;
	}

	public static long addLongAndLongsFromStructWithNestedLongArray(long arg1, MemorySegment arg2) {
		long nestedLongrrayElem1 = arg2.get(JAVA_LONG, 0);
		long nestedLongrrayElem2 = arg2.get(JAVA_LONG, 8);
		long structElem2 = arg2.get(JAVA_LONG, 16);

		long longSum = arg1 + nestedLongrrayElem1 + nestedLongrrayElem2 + structElem2;
		return longSum;
	}

	public static long addLongAndLongsFromStructWithNestedLongArray_reverseOrder(long arg1, MemorySegment arg2) {
		long structElem1 = arg2.get(JAVA_LONG, 0);
		long nestedLongrrayElem1 = arg2.get(JAVA_LONG, 8);
		long nestedLongrrayElem2 = arg2.get(JAVA_LONG, 16);

		long longSum = arg1 + structElem1 + nestedLongrrayElem1 + nestedLongrrayElem2;
		return longSum;
	}

	public static long addLongAndLongsFromStructWithNestedStructArray(long arg1, MemorySegment arg2) {
		long nestedStructArrayElem1_Elem1 = arg2.get(JAVA_LONG, 0);
		long nestedStructArrayElem1_Elem2 = arg2.get(JAVA_LONG, 8);
		long nestedStructArrayElem2_Elem1 = arg2.get(JAVA_LONG, 16);
		long nestedStructArrayElem2_Elem2 = arg2.get(JAVA_LONG, 24);
		long structElem2 = arg2.get(JAVA_LONG, 32);

		long longSum = arg1 + structElem2
				+ nestedStructArrayElem1_Elem1 + nestedStructArrayElem1_Elem2
				+ nestedStructArrayElem2_Elem1 + nestedStructArrayElem2_Elem2;
		return longSum;
	}

	public static long addLongAndLongsFromStructWithNestedStructArray_reverseOrder(long arg1, MemorySegment arg2) {
		long structElem1 = arg2.get(JAVA_LONG, 0);
		long nestedStructArrayElem1_Elem1 = arg2.get(JAVA_LONG, 8);
		long nestedStructArrayElem1_Elem2 = arg2.get(JAVA_LONG, 16);
		long nestedStructArrayElem2_Elem1 = arg2.get(JAVA_LONG, 24);
		long nestedStructArrayElem2_Elem2 = arg2.get(JAVA_LONG, 32);

		long longSum = arg1 + structElem1
				+ nestedStructArrayElem1_Elem1 + nestedStructArrayElem1_Elem2
				+ nestedStructArrayElem2_Elem1 + nestedStructArrayElem2_Elem2;
		return longSum;
	}

	public static MemorySegment add2LongStructs_returnStruct(MemorySegment arg1, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_LONG.withName("elem1"), JAVA_LONG.withName("elem2"));
		MemorySegment longStructSegmt = arena.allocate(structLayout);
		long longStruct_Elem1 = arg1.get(JAVA_LONG, 0) + arg2.get(JAVA_LONG, 0);
		long longStruct_Elem2 = arg1.get(JAVA_LONG, 8) + arg2.get(JAVA_LONG, 8);
		longStructSegmt.set(JAVA_LONG, 0, longStruct_Elem1);
		longStructSegmt.set(JAVA_LONG, 8, longStruct_Elem2);
		return longStructSegmt;
	}

	public static MemorySegment add2LongStructs_returnStructPointer(MemorySegment arg1Addr, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_LONG.withName("elem1"), JAVA_LONG.withName("elem2"));
		MemorySegment arg1Segmt = arg1Addr.reinterpret(structLayout.byteSize());
		long longSum_Elem1 = arg1Segmt.get(JAVA_LONG, 0) + arg2.get(JAVA_LONG, 0);
		long longSum_Elem2 = arg1Segmt.get(JAVA_LONG, 8) + arg2.get(JAVA_LONG, 8);
		arg1Segmt.set(JAVA_LONG, 0, longSum_Elem1);
		arg1Segmt.set(JAVA_LONG, 8, longSum_Elem2);
		return arg1Addr;
	}

	public static MemorySegment add3LongStructs_returnStruct(MemorySegment arg1, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_LONG.withName("elem1"), JAVA_LONG.withName("elem2"), JAVA_LONG.withName("elem3"));
		MemorySegment longStructSegmt = arena.allocate(structLayout);
		long longStruct_Elem1 = arg1.get(JAVA_LONG, 0) + arg2.get(JAVA_LONG, 0);
		long longStruct_Elem2 = arg1.get(JAVA_LONG, 8) + arg2.get(JAVA_LONG, 8);
		long longStruct_Elem3 = arg1.get(JAVA_LONG, 16) + arg2.get(JAVA_LONG, 16);
		longStructSegmt.set(JAVA_LONG, 0, longStruct_Elem1);
		longStructSegmt.set(JAVA_LONG, 8, longStruct_Elem2);
		longStructSegmt.set(JAVA_LONG, 16, longStruct_Elem3);
		return longStructSegmt;
	}

	public static float addFloatAndFloatsFromStruct(float arg1, MemorySegment arg2) {
		float floatSum = arg1 + arg2.get(JAVA_FLOAT, 0) + arg2.get(JAVA_FLOAT, 4);
		return floatSum;
	}

	public static float addFloatAnd5FloatsFromStruct(float arg1, MemorySegment arg2) {
		float floatSum = arg1 + arg2.get(JAVA_FLOAT, 0) + arg2.get(JAVA_FLOAT, 4)
		+ arg2.get(JAVA_FLOAT, 8) + arg2.get(JAVA_FLOAT, 12) + arg2.get(JAVA_FLOAT, 16);
		return floatSum;
	}

	public static float addFloatFromPointerAndFloatsFromStruct(MemorySegment arg1Addr, MemorySegment arg2) {
		MemorySegment arg1Segmt = arg1Addr.reinterpret(JAVA_FLOAT.byteSize());
		float floatSum = arg1Segmt.get(JAVA_FLOAT, 0) + arg2.get(JAVA_FLOAT, 0) + arg2.get(JAVA_FLOAT, 4);
		return floatSum;
	}

	public static MemorySegment addFloatFromPointerAndFloatsFromStruct_returnFloatPointer(MemorySegment arg1Addr, MemorySegment arg2) {
		MemorySegment arg1Segmt = arg1Addr.reinterpret(JAVA_FLOAT.byteSize());
		float floatSum = arg1Segmt.get(JAVA_FLOAT, 0) + arg2.get(JAVA_FLOAT, 0) + arg2.get(JAVA_FLOAT, 4);
		arg1Segmt.set(JAVA_FLOAT, 0, floatSum);
		return arg1Addr;
	}

	public static float addFloatAndFloatsFromStructPointer(float arg1, MemorySegment arg2Addr) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_FLOAT.withName("elem1"), JAVA_FLOAT.withName("elem2"));
		MemorySegment arg2Segmt = arg2Addr.reinterpret(structLayout.byteSize());
		float floatSum = arg1 + arg2Segmt.get(JAVA_FLOAT, 0) + arg2Segmt.get(JAVA_FLOAT, 4);
		return floatSum;
	}

	public static float addFloatAndFloatsFromNestedStruct(float arg1, MemorySegment arg2) {
		float nestedStructElem1 = arg2.get(JAVA_FLOAT, 0);
		float nestedStructElem2 = arg2.get(JAVA_FLOAT, 4);
		float structElem2 = arg2.get(JAVA_FLOAT, 8);

		float floatSum = arg1 + nestedStructElem1 + nestedStructElem2 + structElem2;
		return floatSum;
	}

	public static float addFloatAndFloatsFromNestedStruct_reverseOrder(float arg1, MemorySegment arg2) {
		float structElem1 = arg2.get(JAVA_FLOAT, 0);
		float nestedStructElem1 = arg2.get(JAVA_FLOAT, 4);
		float nestedStructElem2 = arg2.get(JAVA_FLOAT, 8);

		float floatSum = arg1 + structElem1 + nestedStructElem1 + nestedStructElem2;
		return floatSum;
	}

	public static float addFloatAndFloatsFromStructWithNestedFloatArray(float arg1, MemorySegment arg2) {
		float nestedFloatArrayElem1 = arg2.get(JAVA_FLOAT, 0);
		float nestedFloatArrayElem2 = arg2.get(JAVA_FLOAT, 4);
		float structElem2 = arg2.get(JAVA_FLOAT, 8);

		float floatSum = arg1 + nestedFloatArrayElem1 + nestedFloatArrayElem2 + structElem2;
		return floatSum;
	}

	public static float addFloatAndFloatsFromStructWithNestedFloatArray_reverseOrder(float arg1, MemorySegment arg2) {
		float structElem1 = arg2.get(JAVA_FLOAT, 0);
		float nestedFloatArrayElem1 = arg2.get(JAVA_FLOAT, 4);
		float nestedFloatArrayElem2 = arg2.get(JAVA_FLOAT, 8);

		float floatSum = arg1 + structElem1 + nestedFloatArrayElem1 + nestedFloatArrayElem2;
		return floatSum;
	}

	public static float addFloatAndFloatsFromStructWithNestedStructArray(float arg1, MemorySegment arg2) {
		float nestedStructArrayElem1_Elem1 = arg2.get(JAVA_FLOAT, 0);
		float nestedStructArrayElem1_Elem2 = arg2.get(JAVA_FLOAT, 4);
		float nestedStructArrayElem2_Elem1 = arg2.get(JAVA_FLOAT, 8);
		float nestedStructArrayElem2_Elem2 = arg2.get(JAVA_FLOAT, 12);
		float structElem2 = arg2.get(JAVA_FLOAT, 16);

		float floatSum = arg1 + structElem2
				+ nestedStructArrayElem1_Elem1 + nestedStructArrayElem1_Elem2
				+ nestedStructArrayElem2_Elem1 + nestedStructArrayElem2_Elem2;
		return floatSum;
	}

	public static float addFloatAndFloatsFromStructWithNestedStructArray_reverseOrder(float arg1, MemorySegment arg2) {
		float structElem1 = arg2.get(JAVA_FLOAT, 0);
		float nestedStructArrayElem1_Elem1 = arg2.get(JAVA_FLOAT, 4);
		float nestedStructArrayElem1_Elem2 = arg2.get(JAVA_FLOAT, 8);
		float nestedStructArrayElem2_Elem1 = arg2.get(JAVA_FLOAT, 12);
		float nestedStructArrayElem2_Elem2 = arg2.get(JAVA_FLOAT, 16);

		float floatSum = arg1 + structElem1
				+ nestedStructArrayElem1_Elem1 + nestedStructArrayElem1_Elem2
				+ nestedStructArrayElem2_Elem1 + nestedStructArrayElem2_Elem2;
		return floatSum;
	}

	public static MemorySegment add2FloatStructs_returnStruct(MemorySegment arg1, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_FLOAT.withName("elem1"), JAVA_FLOAT.withName("elem2"));
		MemorySegment floatStructSegmt = arena.allocate(structLayout);
		float floatStruct_Elem1 = arg1.get(JAVA_FLOAT, 0) + arg2.get(JAVA_FLOAT, 0);
		float floatStruct_Elem2 = arg1.get(JAVA_FLOAT, 4) + arg2.get(JAVA_FLOAT, 4);
		floatStructSegmt.set(JAVA_FLOAT, 0, floatStruct_Elem1);
		floatStructSegmt.set(JAVA_FLOAT, 4, floatStruct_Elem2);
		return floatStructSegmt;
	}

	public static MemorySegment add2FloatStructs_returnStructPointer(MemorySegment arg1Addr, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_FLOAT.withName("elem1"), JAVA_FLOAT.withName("elem2"));
		MemorySegment arg1Segmt = arg1Addr.reinterpret(structLayout.byteSize());
		float floatSum_Elem1 = arg1Segmt.get(JAVA_FLOAT, 0) + arg2.get(JAVA_FLOAT, 0);
		float floatSum_Elem2 = arg1Segmt.get(JAVA_FLOAT, 4) + arg2.get(JAVA_FLOAT, 4);
		arg1Segmt.set(JAVA_FLOAT, 0, floatSum_Elem1);
		arg1Segmt.set(JAVA_FLOAT, 4, floatSum_Elem2);
		return arg1Addr;
	}

	public static MemorySegment add3FloatStructs_returnStruct(MemorySegment arg1, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_FLOAT.withName("elem1"), JAVA_FLOAT.withName("elem2"), JAVA_FLOAT.withName("elem3"));

		MemorySegment floatStructSegmt = arena.allocate(structLayout);
		float floatStruct_Elem1 = arg1.get(JAVA_FLOAT, 0) + arg2.get(JAVA_FLOAT, 0);
		float floatStruct_Elem2 = arg1.get(JAVA_FLOAT, 4) + arg2.get(JAVA_FLOAT, 4);
		float floatStruct_Elem3 = arg1.get(JAVA_FLOAT, 8) + arg2.get(JAVA_FLOAT, 8);
		floatStructSegmt.set(JAVA_FLOAT, 0, floatStruct_Elem1);
		floatStructSegmt.set(JAVA_FLOAT, 4, floatStruct_Elem2);
		floatStructSegmt.set(JAVA_FLOAT, 8, floatStruct_Elem3);
		return floatStructSegmt;
	}

	public static double addDoubleAndDoublesFromStruct(double arg1, MemorySegment arg2) {
		double doubleSum = arg1 + arg2.get(JAVA_DOUBLE, 0) + arg2.get(JAVA_DOUBLE, 8);
		return doubleSum;
	}

	public static double addDoubleFromPointerAndDoublesFromStruct(MemorySegment arg1Addr, MemorySegment arg2) {
		MemorySegment arg1Segmt = arg1Addr.reinterpret(JAVA_DOUBLE.byteSize());
		double doubleSum = arg1Segmt.get(JAVA_DOUBLE, 0) + arg2.get(JAVA_DOUBLE, 0) + arg2.get(JAVA_DOUBLE, 8);
		return doubleSum;
	}

	public static MemorySegment addDoubleFromPointerAndDoublesFromStruct_returnDoublePointer(MemorySegment arg1Addr, MemorySegment arg2) {
		MemorySegment arg1Segmt = arg1Addr.reinterpret(JAVA_DOUBLE.byteSize());
		double doubleSum = arg1Segmt.get(JAVA_DOUBLE, 0) + arg2.get(JAVA_DOUBLE, 0) + arg2.get(JAVA_DOUBLE, 8);
		arg1Segmt.set(JAVA_DOUBLE, 0, doubleSum);
		return arg1Addr;
	}

	public static double addDoubleAndDoublesFromStructPointer(double arg1, MemorySegment arg2Addr) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_DOUBLE.withName("elem1"), JAVA_DOUBLE.withName("elem2"));
		MemorySegment arg2Segmt = arg2Addr.reinterpret(structLayout.byteSize());
		double doubleSum = arg1 + arg2Segmt.get(JAVA_DOUBLE, 0) + arg2Segmt.get(JAVA_DOUBLE, 8);
		return doubleSum;
	}

	public static double addDoubleAndDoublesFromNestedStruct(double arg1, MemorySegment arg2) {
		double nestedStructElem1 = arg2.get(JAVA_DOUBLE, 0);
		double nestedStructElem2 = arg2.get(JAVA_DOUBLE, 8);
		double structElem2 = arg2.get(JAVA_DOUBLE, 16);

		double doubleSum = arg1 + nestedStructElem1 + nestedStructElem2 + structElem2;
		return doubleSum;
	}

	public static double addDoubleAndDoublesFromNestedStruct_reverseOrder(double arg1, MemorySegment arg2) {
		double structElem1 = arg2.get(JAVA_DOUBLE, 0);
		double nestedStructElem1 = arg2.get(JAVA_DOUBLE, 8);
		double nestedStructElem2 = arg2.get(JAVA_DOUBLE, 16);

		double doubleSum = arg1 + structElem1 + nestedStructElem1 + nestedStructElem2;
		return doubleSum;
	}

	public static double addDoubleAndDoublesFromStructWithNestedDoubleArray(double arg1, MemorySegment arg2) {
		double nestedDoubleArrayElem1 = arg2.get(JAVA_DOUBLE, 0);
		double nestedDoubleArrayElem2 = arg2.get(JAVA_DOUBLE, 8);
		double structElem2 = arg2.get(JAVA_DOUBLE, 16);

		double doubleSum = arg1 + nestedDoubleArrayElem1 + nestedDoubleArrayElem2 + structElem2;
		return doubleSum;
	}

	public static double addDoubleAndDoublesFromStructWithNestedDoubleArray_reverseOrder(double arg1, MemorySegment arg2) {
		double structElem1 = arg2.get(JAVA_DOUBLE, 0);
		double nestedDoubleArrayElem1 = arg2.get(JAVA_DOUBLE, 8);
		double nestedDoubleArrayElem2 = arg2.get(JAVA_DOUBLE, 16);

		double doubleSum = arg1 + structElem1 + nestedDoubleArrayElem1 + nestedDoubleArrayElem2;
		return doubleSum;
	}

	public static double addDoubleAndDoublesFromStructWithNestedStructArray(double arg1, MemorySegment arg2) {
		double nestedStructArrayElem1_Elem1 = arg2.get(JAVA_DOUBLE, 0);
		double nestedStructArrayElem1_Elem2 = arg2.get(JAVA_DOUBLE, 8);
		double nestedStructArrayElem2_Elem1 = arg2.get(JAVA_DOUBLE, 16);
		double nestedStructArrayElem2_Elem2 = arg2.get(JAVA_DOUBLE, 24);
		double structElem2 = arg2.get(JAVA_DOUBLE, 32);

		double doubleSum = arg1 + structElem2
				+ nestedStructArrayElem1_Elem1 + nestedStructArrayElem1_Elem2
				+ nestedStructArrayElem2_Elem1 + nestedStructArrayElem2_Elem2;
		return doubleSum;
	}

	public static double addDoubleAndDoublesFromStructWithNestedStructArray_reverseOrder(double arg1, MemorySegment arg2) {
		double structElem1 = arg2.get(JAVA_DOUBLE, 0);
		double nestedStructArrayElem1_Elem1 = arg2.get(JAVA_DOUBLE, 8);
		double nestedStructArrayElem1_Elem2 = arg2.get(JAVA_DOUBLE, 16);
		double nestedStructArrayElem2_Elem1 = arg2.get(JAVA_DOUBLE, 24);
		double nestedStructArrayElem2_Elem2 = arg2.get(JAVA_DOUBLE, 32);

		double doubleSum = arg1 + structElem1
				+ nestedStructArrayElem1_Elem1 + nestedStructArrayElem1_Elem2
				+ nestedStructArrayElem2_Elem1 + nestedStructArrayElem2_Elem2;
		return doubleSum;
	}

	public static MemorySegment add2DoubleStructs_returnStruct(MemorySegment arg1, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_DOUBLE.withName("elem1"), JAVA_DOUBLE.withName("elem2"));
		MemorySegment doubleStructSegmt = arena.allocate(structLayout);
		double doubleStruct_Elem1 = arg1.get(JAVA_DOUBLE, 0) + arg2.get(JAVA_DOUBLE, 0);
		double doubleStruct_Elem2 = arg1.get(JAVA_DOUBLE, 8) + arg2.get(JAVA_DOUBLE, 8);
		doubleStructSegmt.set(JAVA_DOUBLE, 0, doubleStruct_Elem1);
		doubleStructSegmt.set(JAVA_DOUBLE, 8, doubleStruct_Elem2);
		return doubleStructSegmt;
	}

	public static MemorySegment add2DoubleStructs_returnStructPointer(MemorySegment arg1Addr, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_DOUBLE.withName("elem1"), JAVA_DOUBLE.withName("elem2"));
		MemorySegment arg1Segmt = arg1Addr.reinterpret(structLayout.byteSize());
		double doubleSum_Elem1 = arg1Segmt.get(JAVA_DOUBLE, 0) + arg2.get(JAVA_DOUBLE, 0);
		double doubleSum_Elem2 = arg1Segmt.get(JAVA_DOUBLE, 8) + arg2.get(JAVA_DOUBLE, 8);
		arg1Segmt.set(JAVA_DOUBLE, 0, doubleSum_Elem1);
		arg1Segmt.set(JAVA_DOUBLE, 8, doubleSum_Elem2);
		return arg1Addr;
	}

	public static MemorySegment add3DoubleStructs_returnStruct(MemorySegment arg1, MemorySegment arg2) {
		GroupLayout structLayout = MemoryLayout.structLayout(JAVA_DOUBLE.withName("elem1"), JAVA_DOUBLE.withName("elem2"), JAVA_DOUBLE.withName("elem3"));
		MemorySegment doubleStructSegmt = arena.allocate(structLayout);
		double doubleStruct_Elem1 = arg1.get(JAVA_DOUBLE, 0) + arg2.get(JAVA_DOUBLE, 0);
		double doubleStruct_Elem2 = arg1.get(JAVA_DOUBLE, 8) + arg2.get(JAVA_DOUBLE, 8);
		double doubleStruct_Elem3 = arg1.get(JAVA_DOUBLE, 16) + arg2.get(JAVA_DOUBLE, 16);
		doubleStructSegmt.set(JAVA_DOUBLE, 0, doubleStruct_Elem1);
		doubleStructSegmt.set(JAVA_DOUBLE, 8, doubleStruct_Elem2);
		doubleStructSegmt.set(JAVA_DOUBLE, 16, doubleStruct_Elem3);
		return doubleStructSegmt;
	}

	public static int addIntAndIntShortFromStruct(int arg1, MemorySegment arg2) {
		int intSum = arg1 + arg2.get(JAVA_INT, 0) + arg2.get(JAVA_SHORT, 4);
		return intSum;
	}

	public static int addIntAndShortIntFromStruct(int arg1, MemorySegment arg2) {
		int intSum = arg1 + arg2.get(JAVA_SHORT, 0) + arg2.get(JAVA_INT, 4);
		return intSum;
	}

	public static long addIntAndIntLongFromStruct(int arg1, MemorySegment arg2) {
		long longSum = arg1 + arg2.get(JAVA_INT, 0) + arg2.get(JAVA_LONG, 8);
		return longSum;
	}

	public static long addIntAndLongIntFromStruct(int arg1, MemorySegment arg2) {
		long longSum = arg1 + arg2.get(JAVA_LONG, 0) + arg2.get(JAVA_INT, 8);
		return longSum;
	}

	public static double addDoubleAndIntDoubleFromStruct(double arg1, MemorySegment arg2) {
		int structElem1 = arg2.get(JAVA_INT, 0);
		/* The size of [int, double] on AIX/PPC 64-bit is 12 bytes without padding by default
		 * while the same struct is 16 bytes with padding on other platforms.
		 */
		double structElem2 = arg2.get(JAVA_DOUBLE, isAixOS ? 4 : 8);
		double doubleSum = arg1 + structElem1 + structElem2;
		return doubleSum;
	}

	public static double addDoubleAndDoubleIntFromStruct(double arg1, MemorySegment arg2) {
		double doubleSum = arg1 + arg2.get(JAVA_DOUBLE, 0) + arg2.get(JAVA_INT, 8);
		return doubleSum;
	}

	public static double addDoubleAndFloatDoubleFromStruct(double arg1, MemorySegment arg2) {
		float structElem1 = arg2.get(JAVA_FLOAT, 0);
		/* The size of [float, double] on AIX/PPC 64-bit is 12 bytes without padding by default
		 * while the same struct is 16 bytes with padding on other platforms.
		 */
		double structElem2 = arg2.get(JAVA_DOUBLE, isAixOS ? 4 : 8);
		double doubleSum = arg1 + structElem1 + structElem2;
		return doubleSum;
	}

	public static double addDoubleAndDoubleFloatFromStruct(double arg1, MemorySegment arg2) {
		double doubleSum = arg1 + arg2.get(JAVA_DOUBLE, 0) + arg2.get(JAVA_FLOAT, 8);
		return doubleSum;
	}

	public static double addDoubleAnd2FloatsDoubleFromStruct(double arg1, MemorySegment arg2) {
		double doubleSum = arg1 + arg2.get(JAVA_FLOAT, 0) + arg2.get(JAVA_FLOAT, 4) + arg2.get(JAVA_DOUBLE, 8);
		return doubleSum;
	}

	public static double addDoubleAndDouble2FloatsFromStruct(double arg1, MemorySegment arg2) {
		double doubleSum = arg1 + arg2.get(JAVA_DOUBLE, 0) + arg2.get(JAVA_FLOAT, 8) + arg2.get(JAVA_FLOAT, 12);
		return doubleSum;
	}

	public static float addFloatAndInt2FloatsFromStruct(float arg1, MemorySegment arg2) {
		float floatSum = arg1 + arg2.get(JAVA_INT, 0) + arg2.get(JAVA_FLOAT, 4) + arg2.get(JAVA_FLOAT, 8);
		return floatSum;
	}

	public static float addFloatAndFloatIntFloatFromStruct(float arg1, MemorySegment arg2) {
		float structElem2 = Integer.valueOf(arg2.get(JAVA_INT, 4)).floatValue();
		float floatSum = arg1 + arg2.get(JAVA_FLOAT, 0) + structElem2 + arg2.get(JAVA_FLOAT, 8);
		return floatSum;
	}

	public static double addDoubleAndIntFloatDoubleFromStruct(double arg1, MemorySegment arg2) {
		double doubleSum = arg1 + arg2.get(JAVA_INT, 0) + arg2.get(JAVA_FLOAT, 4) + arg2.get(JAVA_DOUBLE, 8);
		return doubleSum;
	}

	public static double addDoubleAndFloatIntDoubleFromStruct(double arg1, MemorySegment arg2) {
		double doubleSum = arg1 + arg2.get(JAVA_FLOAT, 0) + arg2.get(JAVA_INT, 4) + arg2.get(JAVA_DOUBLE, 8);
		return doubleSum;
	}

	public static double addDoubleAndLongDoubleFromStruct(double arg1, MemorySegment arg2) {
		double doubleSum = arg1 + arg2.get(JAVA_LONG, 0) + arg2.get(JAVA_DOUBLE, 8);
		return doubleSum;
	}

	public static float addFloatAndInt3FloatsFromStruct(float arg1, MemorySegment arg2) {
		float floatSum = arg1 + arg2.get(JAVA_INT, 0) + arg2.get(JAVA_FLOAT, 4)
		+ arg2.get(JAVA_FLOAT, 8) + arg2.get(JAVA_FLOAT, 12);
		return floatSum;
	}

	public static long addLongAndLong2FloatsFromStruct(long arg1, MemorySegment arg2) {
		long structElem1 = arg2.get(JAVA_LONG, 0);
		long structElem2 = Float.valueOf(arg2.get(JAVA_FLOAT, 8)).longValue();
		long structElem3 = Float.valueOf(arg2.get(JAVA_FLOAT, 12)).longValue();
		long longSum = arg1 + structElem1 + structElem2 + structElem3;
		return longSum;
	}

	public static float addFloatAnd3FloatsIntFromStruct(float arg1, MemorySegment arg2) {
		float floatSum = arg1 + arg2.get(JAVA_FLOAT, 0) + arg2.get(JAVA_FLOAT, 4)
		+ arg2.get(JAVA_FLOAT, 8) + arg2.get(JAVA_INT, 12);
		return floatSum;
	}

	public static long addLongAndFloatLongFromStruct(long arg1, MemorySegment arg2) {
		long structElem1 = Float.valueOf(arg2.get(JAVA_FLOAT, 0)).longValue();
		long structElem2 = arg2.get(JAVA_LONG, 8);
		long longSum = arg1 + structElem1 + structElem2;
		return longSum;
	}

	public static double addDoubleAndDoubleFloatIntFromStruct(double arg1, MemorySegment arg2) {
		double doubleSum = arg1 + arg2.get(JAVA_DOUBLE, 0) + arg2.get(JAVA_FLOAT, 8) + arg2.get(JAVA_INT, 12);
		return doubleSum;
	}

	public static double addDoubleAndDoubleLongFromStruct(double arg1, MemorySegment arg2) {
		double doubleSum = arg1 + arg2.get(JAVA_DOUBLE, 0) + arg2.get(JAVA_LONG, 8);
		return doubleSum;
	}

	public static long addLongAnd2FloatsLongFromStruct(long arg1, MemorySegment arg2) {
		long structElem1 = Float.valueOf(arg2.get(JAVA_FLOAT, 0)).longValue();
		long structElem2 = Float.valueOf(arg2.get(JAVA_FLOAT, 4)).longValue();
		long structElem3 = arg2.get(JAVA_LONG, 8);
		long longSum = arg1 + structElem1 + structElem2 + structElem3;
		return longSum;
	}

	public static short addShortAnd3ShortsCharFromStruct(short arg1, MemorySegment arg2) {
		short shortSum = (short)(arg1 + arg2.get(JAVA_SHORT, 0) + arg2.get(JAVA_SHORT, 2)
		+ arg2.get(JAVA_SHORT, 4) + arg2.get(JAVA_CHAR, 6));
		return shortSum;
	}

	public static float addFloatAndIntFloatIntFloatFromStruct(float arg1, MemorySegment arg2) {
		float floatSum = arg1 + arg2.get(JAVA_INT, 0) + arg2.get(JAVA_FLOAT, 4)+ arg2.get(JAVA_INT, 8) + arg2.get(JAVA_FLOAT, 12);
		return floatSum;
	}

	public static double addDoubleAndIntDoubleFloatFromStruct(double arg1, MemorySegment arg2) {
		int structElem1 = arg2.get(JAVA_INT, 0);
		/* The size of [int, double, float] on AIX/PPC 64-bit is 16 bytes without padding by default
		 * while the same struct is 20 bytes with padding on other platforms.
		 */
		double structElem2 = arg2.get(JAVA_DOUBLE, isAixOS ? 4 : 8);
		float structElem3 = arg2.get(JAVA_FLOAT, isAixOS ? 12 : 16);
		double doubleSum = arg1 + structElem1 + structElem2 + structElem3;
		return doubleSum;
	}

	public static double addDoubleAndFloatDoubleIntFromStruct(double arg1, MemorySegment arg2) {
		float structElem1 = arg2.get(JAVA_FLOAT, 0);
		/* The size of [float, double, int] on AIX/PPC 64-bit is 16 bytes without padding by default
		 * while the same struct is 20 bytes with padding on other platforms.
		 */
		double structElem2 = arg2.get(JAVA_DOUBLE, isAixOS ? 4 : 8);
		int structElem3 = arg2.get(JAVA_INT, isAixOS ? 12 : 16);
		double doubleSum = arg1 + structElem1 + structElem2 + structElem3;
		return doubleSum;
	}

	public static double addDoubleAndIntDoubleIntFromStruct(double arg1, MemorySegment arg2) {
		int structElem1 = arg2.get(JAVA_INT, 0);
		/* The size of [int, double, int] on AIX/PPC 64-bit is 16 bytes without padding by default
		 * while the same struct is 20 bytes with padding on other platforms.
		 */
		double structElem2 = arg2.get(JAVA_DOUBLE, isAixOS ? 4 : 8);
		int structElem3 = arg2.get(JAVA_INT, isAixOS ? 12 : 16);
		double doubleSum = arg1 + structElem1 + structElem2 + structElem3;
		return doubleSum;
	}

	public static double addDoubleAndFloatDoubleFloatFromStruct(double arg1, MemorySegment arg2) {
		float structElem1 = arg2.get(JAVA_FLOAT, 0);
		/* The size of [float, double, float] on AIX/PPC 64-bit is 16 bytes without padding by default
		 * while the same struct is 20 bytes with padding on other platforms.
		 */
		double structElem2 = arg2.get(JAVA_DOUBLE, isAixOS ? 4 : 8);
		float structElem3 = arg2.get(JAVA_FLOAT, isAixOS ? 12 : 16);
		double doubleSum = arg1 + structElem1 + structElem2 + structElem3;
		return doubleSum;
	}

	public static double addDoubleAndIntDoubleLongFromStruct(double arg1, MemorySegment arg2) {
		int structElem1 = arg2.get(JAVA_INT, 0);
		/* The padding in the struct [int, double, long] on AIX/PPC 64-bit is different from
		 * other platforms as follows:
		 * 1) there is no padding between int and double.
		 * 2) there is a 4-byte padding between double and long.
		 */
		double structElem2 = arg2.get(JAVA_DOUBLE, isAixOS ? 4 : 8);
		double structElem3 = arg2.get(JAVA_LONG, 16);
		double doubleSum = arg1 + structElem1 + structElem2 + structElem3;
		return doubleSum;
	}

	public static MemorySegment return254BytesFromStruct() {
		SequenceLayout byteArray = MemoryLayout.sequenceLayout(254, JAVA_BYTE);
		GroupLayout structLayout = MemoryLayout.structLayout(byteArray);
		MemorySegment byteArrStruSegment = arena.allocate(structLayout);

		for (int i = 0; i < 254; i++) {
			byteArrStruSegment.set(JAVA_BYTE, i, (byte)i);
		}
		return byteArrStruSegment;
	}

	public static MemorySegment return4KBytesFromStruct() {
		SequenceLayout byteArray = MemoryLayout.sequenceLayout(4096, JAVA_BYTE);
		GroupLayout structLayout = MemoryLayout.structLayout(byteArray);
		MemorySegment byteArrStruSegment = arena.allocate(structLayout);

		for (int i = 0; i < 4096; i++) {
			byteArrStruSegment.set(JAVA_BYTE, i, (byte)i);
		}
		return byteArrStruSegment;
	}

	public static byte addNegBytesFromStruct(byte arg1, MemorySegment arg2, byte arg3, byte arg4) {
		byte arg2_elem1 = arg2.get(JAVA_BYTE, 0);
		byte arg2_elem2 = arg2.get(JAVA_BYTE, 1);

		Assert.assertEquals((byte)-6, ((Byte)arg1).byteValue());
		Assert.assertEquals((byte)-8, ((Byte)arg2_elem1).byteValue());
		Assert.assertEquals((byte)-9, ((Byte)arg2_elem2).byteValue());
		Assert.assertEquals((byte)-8, ((Byte)arg3).byteValue());
		Assert.assertEquals((byte)-9, ((Byte)arg4).byteValue());

		byte byteSum = (byte)(arg1 + arg2_elem1 + arg2_elem2 + arg3 + arg4);
		return byteSum;
	}

	public static short addNegShortsFromStruct(short arg1,  MemorySegment arg2, short arg3, short arg4) {
		short arg2_elem1 = arg2.get(JAVA_SHORT, 0);
		short arg2_elem2 = arg2.get(JAVA_SHORT, 2);

		Assert.assertEquals((short)-777, ((Short)arg1).shortValue());
		Assert.assertEquals((short)-888, ((Short)arg2_elem1).shortValue());
		Assert.assertEquals((short)-999, ((Short)arg2_elem2).shortValue());
		Assert.assertEquals((short)-888, ((Short)arg3).shortValue());
		Assert.assertEquals((short)-999, ((Short)arg4).shortValue());

		short shortSum = (short)(arg1 + arg2_elem1 + arg2_elem2 + arg3 + arg4);
		return shortSum;
	}

	public static int captureTrivialOption(int intArg1) {
		Assert.fail("The method shouldn't be invoked during the trivial downcall.");
		return intArg1;
	}
}
