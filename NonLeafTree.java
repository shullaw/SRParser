//NonLeafTree is used to represent E, T, and F
class NonLeafTree extends ParseTree {
    String nonLeaf;
    ParseTree[] children;

    public NonLeafTree(String nonLeaf) {
        this.nonLeaf = nonLeaf;
        children = new ParseTree[0];
    }

    public void addChild(ParseTree child) {
        ParseTree[] newChildren = new ParseTree[children.length + 1];
        for (int i = 0; i < children.length; i++)
            newChildren[i] = children[i];
        newChildren[children.length] = child;
        children = newChildren;
    }

    public String toString() {
        return nonLeaf;
    }
}
