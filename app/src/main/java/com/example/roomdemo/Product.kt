class Product {
    var id: Int = 0
    var productName: String = ""
    var quantity: Int = 0
    constructor()
    constructor(productname: String, quantity: Int) {
        this.productName = productname
        this.quantity = quantity
    }
}

