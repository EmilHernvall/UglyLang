UglyLang
========

Toy programming language. Work in progress. Most of what's in this document is
not yet implemented.

Features:

 * Static typing
 * First-class functions
 * Class-based objects
   * No implementation inheritence
   * Explicit interfaces
   * Built-in delegation
 * Overloadable operators
 * Call-by-value with copy-on-write, unless ref keyword is used

Built-in types:

 * func
 * string
 * int
 * fixed
 * boolean
 * array
 * tuples (haskell-like)

All types are objects. Types can take type parameters. These are remembered at
runtime.

Statements:

 * declarations
 * if
 * for

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

Subscript:

    []

Priority:

    ()

Functions
---------

A function signature looks like this:

return-type(param-type param-name, ...)

Functions are always stored in a variable. When first declaring a function, the
"func" type indicator can be used. In any other case the full signature has
to be used.

For example:

    func map =
        iter<int>(iter<int> l, int(int) f) {
            iter<int> out = list<int>();
            for i in l {
                out.add(f(i));
            }
            return i;
        };

    iter<int> nums = range(1, 10);
    iter<int> doubled =
        map(nums,
            int(int a) {
                return 2*a;
            });

Classes
-------

Methods and member variables can be public or private, which are separated into
sections. Only one public and one private section are allowed. The public
section _has_ to be present.

Static methods are not supported.

Classes can accept type parameters. These are kept at runtime.

Classes _cannot_ be nested.

Class syntax example:

    class arraylist<e>(list<e>, subscriptable<e>)
    {
    public:
        // init is a reserved name for the constructor. The return type must always
        // equal the class name.
        func init =
            List() {
                // ...
            };

        func add =
            void(ref e add) {
                // ...
            };

        func get =
            e(int idx) {
                // ...
            };

        func size =
            int() {
                // ...
            };

    private:
        int size;
    };

Interface syntax example:

    interface list<e>(iter<e>)
    {
        void(e) add;
        e(int) get;
        int() size;
    };

Usage example:

    list<int> intList = arraylist<int>();
    intList.add(123);
    int foo = intList.get(0);
