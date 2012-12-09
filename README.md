UglyLang
========

Toy programming language. Work in progress. Most of what's in this document is
not yet implemented.

Planned Features:

 * Static typing
 * First-class functions
 * Prototype-based objects
   * No implementation inheritence
   * Explicit interfaces
   * Built-in delegation (unimplemented)
 * Overloadable operators (unimplemented)
 * Call-by-value with copy-on-write, unless ref keyword is used (unimplemented)

Built-in types:

 * func (alias when declaring)
 * string
 * int
 * fixed (for decimal)
 * boolean
 * array
 * tuples (haskell-like)
 * named tuples

Statements:

 * declarations
 * if
 * for (unimplemented)
 * each (unimplemented)
 * type (unimplemented)

Operators
---------

Arithmetic:

     + Addition
     - Subtraction
     * Multiplication
     / Division
     ^ Exponentation
     % Modulus

Comparison:

    == Equals
    != Unequal
    > Larger than
    < Less than
    >= Larger than or equal
    <= Less than or equal

Logical:

These are overloaded and behave differently for different types. They
short-circuit for booleans, while they are bit-wise operators for integers.

    and
    or
    not
    xor

Subscript and indexing:

    []
    .

Priority:

    ()

Arrays
------

Arrays can be defined for any type by appending [].

    int[] arr = [ 2,3,5,7,11 ];

An empty array of specified size can be created using:

    int[] arr = array(100, 0);

In this case 100 is the size, and 0 is the default value.

An automatically growing array can be created using:

    int[] arr = autoarray(0);

0 is the default value.

Values can be set and retrieved using the subscript operator:

    arr[0] = 7;
    int foo = arr[0];

Tuples and named tuples
-----------------------

Tuples have a type signature which consists of a list of subtypes surrounded
by paranthesis. They are declared and initialized like this:

    (string, int, int) myTuple = ("foo", 7, 11);

A named tuple is defined like this:

    (name:string, age:int, height:int) myNamedTuple =
        (name:"emil", age:26, height:188);

Not all keys of a named tuple has to be declared in the type signature, but all
keys in the type signature has to be present in the instance of the tuple.

Both tuples and named tuples are dereferences using the subscript operator:

    string val1 = myTuple[0];
    string val2 = myTuple.name;

Note that tuples cannot be dynamically dereferenced since this would make it
impossible to determine the type at compile time. This is however possible for
arrays.

Functions
---------

A function signature looks like this:

    (return-type)(param-type param-name, ...)

Functions are always stored in a variable. When first declaring a function, the
"func" type indicator can be used. In any other case the full signature has
to be used.

For example:

    (int)(int,int) fastexp =
        (int)(int num, int pow) {
            if (pow == 1) {
                return num;
            }
            if (pow % 2 == 0) {
                return fastexp(num*num, pow/2);
            }
            return num*fastexp(num*num, (pow - 1)/2);
        };

    func factorial =
        (int)(int num) {
            if (num == 1) {
                return num;
            }
            return num*factorial(num-1);
        };

func is only a valid type when a function is first declared, and will be
substituted for the full type.

Overloading of functions can be achived by "adding" multiple functions. The 
return type must be the same for all the overloaded functions.

    func testFunc =
            (string)(int a) {
                return intToStr(a);
            } 
            +
            (string)(string a) {
                return a;
            };

    string foo = testFunc(77);
    string bar = testFunc("hello");

Type aliases
------------

A type can be given a name using type aliases:

    type MyFunc (string)(string, int, int);
    type MyFuncArr MyFunc[];

Control structures
------------------

If statements:

    if a == b {
        /* code */
    } elif a == c {
        /* code */
    } else {
        /* code */
    }

While statements:

    while (a < len) {
        /* code */
    }

An each loop can be applied to name tuples defining the get:(type)(int) and
size:(int)() methods.

    int[] arr = [1,2,3];
    each (int a : arr) {
        /* code */
    }

Objects
-------

Named tuples are used to construct objects. An object is a named tuple with
fields containing functions. All other fields of the tuple can be accessed from
within it. Externally only the keys specified in the type signature will be
available. The named tuple type is thus the object interface.

    type Greeter (name:string, new:(Greeter)(string), greet:(void)());

    Greeter greeterImpl =
        (
            name: "",
            new: (Greeter)(string name) {
                 Greeter newGreeter = copy(self);
                 newGreeter["name"] = name;
            },
            greet: (void)() {
                print "Hello, " + name + "!";
            }
        );

    Greeter myGreeter = greeterImpl.new();
    myGreeter.name = "Emil";
    myGreeter.greet();
