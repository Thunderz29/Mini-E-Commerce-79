import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NavbarComponent } from '../../components/navbar/navbar.component';

@Component({
  standalone: true,
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
  imports: [CommonModule, FormsModule, NavbarComponent]
})
export class CartComponent {
  cart = [
    { id: 1, name: 'Laptop', price: 7000000, qty: 1, image: '/images/product.jpg', checked: false },
    { id: 2, name: 'Headphone', price: 500000, qty: 1, image: '/images/product.jpg', checked: false },
    { id: 3, name: 'Mouse', price: 200000, qty: 1, image: '/images/product.jpg', checked: false }
  ];

  selectedTotal = 0;
  overallTotal = 0;

  constructor(private router: Router) {
    this.calculateTotal();
  }

  calculateTotal() {
    this.selectedTotal = this.cart
      .filter(item => item.checked)
      .reduce((sum, item) => sum + (item.qty * item.price), 0);

    this.overallTotal = this.cart.reduce((sum, item) => sum + (item.qty * item.price), 0);
  }

  increaseQty(index: number) {
    this.cart[index].qty++;
    this.calculateTotal();
  }

  decreaseQty(index: number) {
    if (this.cart[index].qty > 1) {
      this.cart[index].qty--;
      this.calculateTotal();
    }
  }

  removeItem(index: number) {
    this.cart.splice(index, 1);
    this.calculateTotal();
  }

  checkout() {
    alert(`Checkout berhasil! Total yang harus dibayar: Rp ${this.selectedTotal.toLocaleString()}`);
    this.router.navigate(['/payment']); // Arahkan ke halaman pembayaran
  }
}
