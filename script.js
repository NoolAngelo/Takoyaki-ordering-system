// Gelo's Takoyaki Professional POS System - JavaScript Implementation
// Version 2.0 - Web Application

// Global State Management
class TakoyakiPOS {
  constructor() {
    this.currentOrder = [];
    this.customer = null;
    this.appliedDiscount = null;
    this.orderCounter = 1;
    this.orderHistory = [];
    this.currentPaymentMethod = "cash";

    // Menu data matching the Java application
    this.menuItems = [
      // Takoyaki Items (1-6)
      {
        id: 1,
        name: "Original Takoyaki",
        price: 50.0,
        category: "takoyaki",
        stock: 50,
        description: "Classic octopus balls with traditional sauce",
      },
      {
        id: 2,
        name: "Cheese Takoyaki",
        price: 60.0,
        category: "takoyaki",
        stock: 45,
        description: "Melted cheese inside crispy takoyaki",
      },
      {
        id: 3,
        name: "Spicy Takoyaki",
        price: 55.0,
        category: "takoyaki",
        stock: 40,
        description: "Extra spicy with jalapeños and hot sauce",
      },
      {
        id: 4,
        name: "Deluxe Takoyaki",
        price: 70.0,
        category: "takoyaki",
        stock: 35,
        description: "Premium ingredients with extra toppings",
      },
      {
        id: 5,
        name: "Vegetarian Takoyaki",
        price: 45.0,
        category: "takoyaki",
        stock: 30,
        description: "Plant-based alternative with vegetables",
      },
      {
        id: 6,
        name: "Jumbo Takoyaki",
        price: 80.0,
        category: "takoyaki",
        stock: 25,
        description: "Extra large portions for big appetites",
      },

      // Combo Meals (7-9)
      {
        id: 7,
        name: "Student Special",
        price: 85.0,
        category: "combo",
        stock: 20,
        description: "6 Original Takoyaki + Soft Drink + Fries",
      },
      {
        id: 8,
        name: "Family Pack",
        price: 250.0,
        category: "combo",
        stock: 15,
        description: "20 Mixed Takoyaki + 4 Drinks + Side Dish",
      },
      {
        id: 9,
        name: "Deluxe Combo",
        price: 150.0,
        category: "combo",
        stock: 18,
        description: "8 Deluxe Takoyaki + Premium Drink + Dessert",
      },

      // Beverages (10-15)
      {
        id: 10,
        name: "Soft Drink",
        price: 25.0,
        category: "drinks",
        stock: 100,
        description: "Coke, Sprite, Orange, or Royal",
      },
      {
        id: 11,
        name: "Iced Tea",
        price: 30.0,
        category: "drinks",
        stock: 80,
        description: "Refreshing homemade iced tea",
      },
      {
        id: 12,
        name: "Fresh Juice",
        price: 40.0,
        category: "drinks",
        stock: 60,
        description: "Orange, Apple, or Mango juice",
      },
      {
        id: 13,
        name: "Hot Coffee",
        price: 35.0,
        category: "drinks",
        stock: 70,
        description: "Freshly brewed coffee",
      },
      {
        id: 14,
        name: "Green Tea",
        price: 30.0,
        category: "drinks",
        stock: 50,
        description: "Premium Japanese green tea",
      },
      {
        id: 15,
        name: "Milkshake",
        price: 50.0,
        category: "drinks",
        stock: 40,
        description: "Chocolate, Vanilla, or Strawberry",
      },

      // Special Options (16-20)
      {
        id: 16,
        name: "Extra Sauce",
        price: 10.0,
        category: "extras",
        stock: 200,
        description: "Additional takoyaki sauce",
      },
      {
        id: 17,
        name: "Side Salad",
        price: 35.0,
        category: "extras",
        stock: 30,
        description: "Fresh mixed greens",
      },
      {
        id: 18,
        name: "Miso Soup",
        price: 25.0,
        category: "extras",
        stock: 40,
        description: "Traditional Japanese soup",
      },
      {
        id: 19,
        name: "French Fries",
        price: 40.0,
        category: "extras",
        stock: 50,
        description: "Crispy golden fries",
      },
      {
        id: 20,
        name: "Ice Cream",
        price: 45.0,
        category: "extras",
        stock: 25,
        description: "Vanilla, Chocolate, or Matcha",
      },
    ];

    // Discount codes
    this.discountCodes = {
      STUDENT10: { percentage: 10, description: "10% off for students" },
      SENIOR15: { percentage: 15, description: "15% off for senior citizens" },
      FIRST20: {
        percentage: 20,
        description: "20% off for first-time customers",
      },
    };

    this.init();
  }

  init() {
    this.renderMenu();
    this.setupEventListeners();
    this.loadOrderHistory();
  }

  setupEventListeners() {
    // Menu filter buttons
    document.querySelectorAll(".filter-btn").forEach((btn) => {
      btn.addEventListener("click", (e) => {
        document
          .querySelectorAll(".filter-btn")
          .forEach((b) => b.classList.remove("active"));
        e.target.classList.add("active");
        this.filterMenu(e.target.dataset.category);
      });
    });

    // Payment method selection
    document.querySelectorAll(".payment-btn").forEach((btn) => {
      btn.addEventListener("click", (e) => {
        document
          .querySelectorAll(".payment-btn")
          .forEach((b) => b.classList.remove("active"));
        e.target.classList.add("active");
        this.currentPaymentMethod = e.target.dataset.method;
        this.updatePaymentInterface();
      });
    });

    // Payment amount input for change calculation
    document.getElementById("paymentAmount").addEventListener("input", (e) => {
      this.calculateChange(parseFloat(e.target.value) || 0);
    });

    // Discount code input
    document.getElementById("discountCode").addEventListener("input", (e) => {
      e.target.value = e.target.value.toUpperCase();
    });
  }

  renderMenu() {
    const menuGrid = document.getElementById("menuGrid");
    menuGrid.innerHTML = "";

    this.menuItems.forEach((item) => {
      const menuItem = document.createElement("div");
      menuItem.className = `menu-item ${
        item.stock === 0 ? "out-of-stock" : ""
      }`;
      menuItem.dataset.category = item.category;

      const stockWarning =
        item.stock <= 10 && item.stock > 0
          ? '<span class="stock-warning">Low Stock</span>'
          : "";
      const outOfStock =
        item.stock === 0
          ? '<span class="out-of-stock-label">Out of Stock</span>'
          : "";

      menuItem.innerHTML = `
                <div class="menu-item-header">
                    <h3>${item.name}</h3>
                    <span class="price">₱${item.price.toFixed(2)}</span>
                </div>
                <p class="description">${item.description}</p>
                <div class="menu-item-footer">
                    <span class="stock">Stock: ${item.stock}</span>
                    ${stockWarning}
                    ${outOfStock}
                    <button class="btn btn-sm btn-primary add-to-order" 
                            onclick="pos.addToOrder(${item.id})" 
                            ${item.stock === 0 ? "disabled" : ""}>
                        <i class="fas fa-plus"></i> Add
                    </button>
                </div>
            `;

      menuGrid.appendChild(menuItem);
    });
  }

  filterMenu(category) {
    const menuItems = document.querySelectorAll(".menu-item");
    menuItems.forEach((item) => {
      if (category === "all" || item.dataset.category === category) {
        item.style.display = "block";
      } else {
        item.style.display = "none";
      }
    });
  }

  addToOrder(itemId) {
    const menuItem = this.menuItems.find((item) => item.id === itemId);
    if (!menuItem || menuItem.stock === 0) {
      this.showToast("Item is out of stock!", "error");
      return;
    }

    const existingItem = this.currentOrder.find((item) => item.id === itemId);
    if (existingItem) {
      if (existingItem.quantity >= menuItem.stock) {
        this.showToast("Cannot add more items. Stock limit reached!", "error");
        return;
      }
      existingItem.quantity++;
      existingItem.totalPrice = existingItem.quantity * existingItem.unitPrice;
    } else {
      this.currentOrder.push({
        id: itemId,
        name: menuItem.name,
        unitPrice: menuItem.price,
        quantity: 1,
        totalPrice: menuItem.price,
      });
    }

    this.renderOrder();
    this.updateTotals();
    this.showToast(`${menuItem.name} added to order!`, "success");
  }

  removeFromOrder(itemId) {
    const itemIndex = this.currentOrder.findIndex((item) => item.id === itemId);
    if (itemIndex > -1) {
      const removedItem = this.currentOrder[itemIndex];
      this.currentOrder.splice(itemIndex, 1);
      this.renderOrder();
      this.updateTotals();
      this.showToast(`${removedItem.name} removed from order!`, "info");
    }
  }

  updateQuantity(itemId, change) {
    const orderItem = this.currentOrder.find((item) => item.id === itemId);
    const menuItem = this.menuItems.find((item) => item.id === itemId);

    if (!orderItem || !menuItem) return;

    const newQuantity = orderItem.quantity + change;

    if (newQuantity <= 0) {
      this.removeFromOrder(itemId);
      return;
    }

    if (newQuantity > menuItem.stock) {
      this.showToast("Cannot exceed available stock!", "error");
      return;
    }

    orderItem.quantity = newQuantity;
    orderItem.totalPrice = orderItem.quantity * orderItem.unitPrice;

    this.renderOrder();
    this.updateTotals();
  }

  renderOrder() {
    const orderItems = document.getElementById("orderItems");

    if (this.currentOrder.length === 0) {
      orderItems.innerHTML = `
                <div class="empty-order">
                    <i class="fas fa-shopping-cart"></i>
                    <p>No items in order</p>
                </div>
            `;
      document.getElementById("checkoutBtn").disabled = true;
      return;
    }

    orderItems.innerHTML = this.currentOrder
      .map(
        (item) => `
            <div class="order-item">
                <div class="order-item-details">
                    <h4>${item.name}</h4>
                    <p>₱${item.unitPrice.toFixed(2)} each</p>
                </div>
                <div class="order-item-controls">
                    <div class="quantity-controls">
                        <button class="btn btn-sm" onclick="pos.updateQuantity(${
                          item.id
                        }, -1)">
                            <i class="fas fa-minus"></i>
                        </button>
                        <span class="quantity">${item.quantity}</span>
                        <button class="btn btn-sm" onclick="pos.updateQuantity(${
                          item.id
                        }, 1)">
                            <i class="fas fa-plus"></i>
                        </button>
                    </div>
                    <div class="item-total">₱${item.totalPrice.toFixed(2)}</div>
                    <button class="btn btn-sm btn-danger" onclick="pos.removeFromOrder(${
                      item.id
                    })">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
        `
      )
      .join("");

    document.getElementById("checkoutBtn").disabled = false;
  }

  updateTotals() {
    const subtotal = this.currentOrder.reduce(
      (sum, item) => sum + item.totalPrice,
      0
    );
    let total = subtotal;

    // Apply discount if any
    let discountAmount = 0;
    if (this.appliedDiscount) {
      discountAmount = subtotal * (this.appliedDiscount.percentage / 100);
      total = subtotal - discountAmount;

      document.getElementById("discountLine").classList.remove("hidden");
      document.getElementById("discountCodeDisplay").textContent =
        this.appliedDiscount.code;
      document.getElementById(
        "discountAmount"
      ).textContent = `-₱${discountAmount.toFixed(2)}`;
    } else {
      document.getElementById("discountLine").classList.add("hidden");
    }

    document.getElementById("subtotal").textContent = `₱${subtotal.toFixed(2)}`;
    document.getElementById("total").textContent = `₱${total.toFixed(2)}`;
  }

  clearOrder() {
    if (this.currentOrder.length === 0) return;

    if (confirm("Are you sure you want to clear the current order?")) {
      this.currentOrder = [];
      this.appliedDiscount = null;
      this.renderOrder();
      this.updateTotals();
      this.showToast("Order cleared!", "info");
    }
  }

  selectDiscount(code) {
    document.getElementById("discountCode").value = code;
  }

  applyDiscountCode() {
    const code = document
      .getElementById("discountCode")
      .value.trim()
      .toUpperCase();

    if (!code) {
      this.showToast("Please enter a discount code!", "error");
      return;
    }

    const discount = this.discountCodes[code];
    if (!discount) {
      this.showToast("Invalid discount code!", "error");
      return;
    }

    // Check if it's a first-time customer discount
    if (code === "FIRST20" && (!this.customer || !this.customer.isFirstTime)) {
      this.showToast(
        "FIRST20 discount is only available for first-time customers!",
        "error"
      );
      return;
    }

    this.appliedDiscount = { code, ...discount };
    this.updateTotals();
    this.closeModal("discountModal");
    this.showToast(`${discount.description} applied!`, "success");
  }

  calculateChange(paymentAmount) {
    const total = this.getOrderTotal();
    const change = paymentAmount - total;

    const changeDisplay = document.getElementById("changeDisplay");
    const changeAmount = document.getElementById("changeAmount");

    if (paymentAmount >= total && paymentAmount > 0) {
      changeDisplay.classList.remove("hidden");
      changeAmount.textContent = `₱${change.toFixed(2)}`;
      changeAmount.style.color = change >= 0 ? "#28a745" : "#dc3545";
    } else {
      changeDisplay.classList.add("hidden");
    }
  }

  getOrderTotal() {
    const subtotal = this.currentOrder.reduce(
      (sum, item) => sum + item.totalPrice,
      0
    );
    let total = subtotal;

    if (this.appliedDiscount) {
      const discountAmount = subtotal * (this.appliedDiscount.percentage / 100);
      total = subtotal - discountAmount;
    }

    return total;
  }

  updatePaymentInterface() {
    const cashPayment = document.getElementById("cashPayment");

    if (this.currentPaymentMethod === "cash") {
      cashPayment.style.display = "block";
    } else {
      cashPayment.style.display = "none";
    }
  }

  processPayment() {
    const total = this.getOrderTotal();
    let isValidPayment = false;

    if (this.currentPaymentMethod === "cash") {
      const paymentAmount =
        parseFloat(document.getElementById("paymentAmount").value) || 0;
      if (paymentAmount < total) {
        this.showToast("Insufficient payment amount!", "error");
        return;
      }
      isValidPayment = true;
    } else {
      // For card and GCash, assume payment is successful
      isValidPayment = true;
    }

    if (isValidPayment) {
      this.completeOrder();
    }
  }

  completeOrder() {
    this.showLoading(true);

    // Simulate processing time
    setTimeout(() => {
      // Update stock levels
      this.currentOrder.forEach((orderItem) => {
        const menuItem = this.menuItems.find(
          (item) => item.id === orderItem.id
        );
        if (menuItem) {
          menuItem.stock -= orderItem.quantity;
        }
      });

      // Create order record
      const order = {
        id: this.generateOrderId(),
        items: [...this.currentOrder],
        customer: this.customer,
        discount: this.appliedDiscount,
        paymentMethod: this.currentPaymentMethod,
        subtotal: this.currentOrder.reduce(
          (sum, item) => sum + item.totalPrice,
          0
        ),
        total: this.getOrderTotal(),
        timestamp: new Date().toISOString(),
        orderNumber: this.orderCounter++,
      };

      this.orderHistory.push(order);
      this.saveOrderHistory();

      this.showLoading(false);
      this.closeModal("paymentModal");
      this.generateReceipt(order);
      this.renderMenu(); // Update stock levels on menu

      this.showToast("Order completed successfully!", "success");
    }, 2000);
  }

  generateReceipt(order) {
    const receipt = document.getElementById("receiptContent");
    const changeAmount =
      this.currentPaymentMethod === "cash"
        ? parseFloat(document.getElementById("paymentAmount").value) -
          order.total
        : 0;

    receipt.innerHTML = `
            <div class="receipt-header">
                <h2>Gelo's Takoyaki</h2>
                <p>Professional POS System</p>
                <p>Thank you for your order!</p>
            </div>
            
            <div class="receipt-details">
                <div class="receipt-line">
                    <span>Order #:</span>
                    <span>${order.id}</span>
                </div>
                <div class="receipt-line">
                    <span>Date:</span>
                    <span>${new Date(order.timestamp).toLocaleString()}</span>
                </div>
                ${
                  order.customer
                    ? `
                <div class="receipt-line">
                    <span>Customer:</span>
                    <span>${order.customer.name}</span>
                </div>
                `
                    : ""
                }
                <div class="receipt-line">
                    <span>Payment:</span>
                    <span>${order.paymentMethod.toUpperCase()}</span>
                </div>
            </div>
            
            <div class="receipt-items">
                <h3>Items Ordered:</h3>
                ${order.items
                  .map(
                    (item) => `
                    <div class="receipt-item">
                        <div class="item-line">
                            <span>${item.name}</span>
                            <span>₱${item.unitPrice.toFixed(2)}</span>
                        </div>
                        <div class="item-details">
                            <span>Qty: ${item.quantity}</span>
                            <span>₱${item.totalPrice.toFixed(2)}</span>
                        </div>
                    </div>
                `
                  )
                  .join("")}
            </div>
            
            <div class="receipt-totals">
                <div class="receipt-line">
                    <span>Subtotal:</span>
                    <span>₱${order.subtotal.toFixed(2)}</span>
                </div>
                ${
                  order.discount
                    ? `
                <div class="receipt-line">
                    <span>Discount (${order.discount.code}):</span>
                    <span>-₱${(
                      (order.subtotal * order.discount.percentage) /
                      100
                    ).toFixed(2)}</span>
                </div>
                `
                    : ""
                }
                <div class="receipt-line total-line">
                    <span><strong>Total:</strong></span>
                    <span><strong>₱${order.total.toFixed(2)}</strong></span>
                </div>
                ${
                  this.currentPaymentMethod === "cash"
                    ? `
                <div class="receipt-line">
                    <span>Cash Paid:</span>
                    <span>₱${(order.total + changeAmount).toFixed(2)}</span>
                </div>
                <div class="receipt-line">
                    <span>Change:</span>
                    <span>₱${changeAmount.toFixed(2)}</span>
                </div>
                `
                    : ""
                }
            </div>
            
            <div class="receipt-footer">
                <p>Thank you for choosing Gelo's Takoyaki!</p>
                <p>Please come again!</p>
            </div>
        `;

    this.showModal("receiptModal");
  }

  generateOrderId() {
    const timestamp = Date.now();
    const random = Math.floor(Math.random() * 1000);
    return `GT${timestamp.toString().slice(-6)}${random
      .toString()
      .padStart(3, "0")}`;
  }

  saveOrderHistory() {
    localStorage.setItem(
      "takoyakiOrderHistory",
      JSON.stringify(this.orderHistory)
    );
    localStorage.setItem("takoyakiOrderCounter", this.orderCounter.toString());
  }

  loadOrderHistory() {
    const savedHistory = localStorage.getItem("takoyakiOrderHistory");
    const savedCounter = localStorage.getItem("takoyakiOrderCounter");

    if (savedHistory) {
      this.orderHistory = JSON.parse(savedHistory);
    }

    if (savedCounter) {
      this.orderCounter = parseInt(savedCounter);
    }
  }

  newOrder() {
    this.currentOrder = [];
    this.customer = null;
    this.appliedDiscount = null;
    this.currentPaymentMethod = "cash";

    this.closeModal("receiptModal");
    this.renderOrder();
    this.updateTotals();
    this.showWelcomeScreen();

    // Reset payment form
    document.getElementById("paymentAmount").value = "";
    document.getElementById("changeDisplay").classList.add("hidden");
  }

  showModal(modalId) {
    document.getElementById(modalId).style.display = "flex";
  }

  closeModal(modalId) {
    document.getElementById(modalId).style.display = "none";
  }

  showLoading(show) {
    const overlay = document.getElementById("loadingOverlay");
    if (show) {
      overlay.classList.remove("hidden");
    } else {
      overlay.classList.add("hidden");
    }
  }

  showToast(message, type = "info") {
    const container = document.getElementById("toastContainer");
    const toast = document.createElement("div");
    toast.className = `toast toast-${type}`;

    const icons = {
      success: "fas fa-check-circle",
      error: "fas fa-exclamation-circle",
      info: "fas fa-info-circle",
      warning: "fas fa-exclamation-triangle",
    };

    toast.innerHTML = `
            <i class="${icons[type]}"></i>
            <span>${message}</span>
        `;

    container.appendChild(toast);

    // Show toast
    setTimeout(() => toast.classList.add("show"), 100);

    // Remove toast after 3 seconds
    setTimeout(() => {
      toast.classList.remove("show");
      setTimeout(() => container.removeChild(toast), 300);
    }, 3000);
  }

  showWelcomeScreen() {
    document.getElementById("welcomeScreen").classList.remove("hidden");
    document.getElementById("posInterface").classList.add("hidden");
  }

  showPOSInterface() {
    document.getElementById("welcomeScreen").classList.add("hidden");
    document.getElementById("posInterface").classList.remove("hidden");
  }
}

// Global Functions for HTML onclick events
let pos;

function startOrdering() {
  pos.showPOSInterface();
  pos.showModal("customerModal");
}

function saveCustomerInfo() {
  const name = document.getElementById("customerName").value.trim();
  const phone = document.getElementById("customerPhone").value.trim();
  const isFirstTime = document.getElementById("isFirstTime").checked;

  if (name) {
    pos.customer = { name, phone, isFirstTime };
    pos.showToast(`Welcome ${name}!`, "success");
  }

  pos.closeModal("customerModal");
}

function skipCustomerInfo() {
  pos.closeModal("customerModal");
}

function applyDiscount() {
  pos.showModal("discountModal");
}

function checkout() {
  if (pos.currentOrder.length === 0) {
    pos.showToast("Please add items to your order first!", "error");
    return;
  }

  // Prepare payment modal
  const total = pos.getOrderTotal();
  document.getElementById("paymentTotal").textContent = `₱${total.toFixed(2)}`;

  // Generate payment summary
  const summary = document.getElementById("paymentOrderSummary");
  summary.innerHTML = pos.currentOrder
    .map(
      (item) => `
        <div class="payment-item">
            <span>${item.name} x${item.quantity}</span>
            <span>₱${item.totalPrice.toFixed(2)}</span>
        </div>
    `
    )
    .join("");

  pos.showModal("paymentModal");
}

function clearOrder() {
  pos.clearOrder();
}

function selectDiscount(code) {
  pos.selectDiscount(code);
}

function applyDiscountCode() {
  pos.applyDiscountCode();
}

function processPayment() {
  pos.processPayment();
}

function printReceipt() {
  window.print();
}

function newOrder() {
  pos.newOrder();
}

function closeModal(modalId) {
  pos.closeModal(modalId);
}

function viewStockLevels() {
  const stockModal = document.getElementById("stockLevels");
  stockModal.innerHTML = `
        <div class="stock-grid">
            ${pos.menuItems
              .map(
                (item) => `
                <div class="stock-item ${item.stock <= 10 ? "low-stock" : ""} ${
                  item.stock === 0 ? "out-of-stock" : ""
                }">
                    <h4>${item.name}</h4>
                    <div class="stock-level">
                        <span class="stock-number">${item.stock}</span>
                        <span class="stock-label">in stock</span>
                    </div>
                    ${
                      item.stock <= 10
                        ? '<span class="stock-warning">Low Stock!</span>'
                        : ""
                    }
                    ${
                      item.stock === 0
                        ? '<span class="out-of-stock-label">Out of Stock</span>'
                        : ""
                    }
                </div>
            `
              )
              .join("")}
        </div>
    `;
  pos.showModal("stockModal");
}

function showSettings() {
  pos.showToast("Settings feature coming soon!", "info");
}

function viewReports() {
  if (pos.orderHistory.length === 0) {
    pos.showToast("No orders in history yet!", "info");
    return;
  }

  const totalRevenue = pos.orderHistory.reduce(
    (sum, order) => sum + order.total,
    0
  );
  const totalOrders = pos.orderHistory.length;
  const averageOrder = totalRevenue / totalOrders;

  pos.showToast(
    `Total Orders: ${totalOrders} | Revenue: ₱${totalRevenue.toFixed(
      2
    )} | Average: ₱${averageOrder.toFixed(2)}`,
    "info"
  );
}

// Initialize the POS system when the page loads
document.addEventListener("DOMContentLoaded", () => {
  pos = new TakoyakiPOS();
});

// Handle modal clicks to close when clicking outside
document.addEventListener("click", (e) => {
  if (e.target.classList.contains("modal")) {
    e.target.style.display = "none";
  }
});

// Keyboard shortcuts
document.addEventListener("keydown", (e) => {
  // Escape key to close modals
  if (e.key === "Escape") {
    document.querySelectorAll(".modal").forEach((modal) => {
      modal.style.display = "none";
    });
  }

  // Enter key to process payment when in payment modal
  if (
    e.key === "Enter" &&
    document.getElementById("paymentModal").style.display === "flex"
  ) {
    processPayment();
  }
});
