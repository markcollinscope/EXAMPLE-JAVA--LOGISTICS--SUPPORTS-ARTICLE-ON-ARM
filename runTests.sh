# Run all unit tests.
## order is important to ensure dependencies are sorted correctly.

PACKAGES="utils ingredients products productLines parsers"
TESTS="utils.testCheckedMap ingredients.testIngredient products.testProduct  productLines.testProductLine parsers.testParsers"

for i in $PACKAGES; do
    rm -f $i/*.class
    echo -n "."
    javac $i/*.java 
done

echo

for i in $TESTS; do
   echo $i
   java org.junit.runner.JUnitCore $i
done
