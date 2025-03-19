import { CommonModule, DecimalPipe } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../../components/navbar/navbar.component';

@Component({
  selector: 'app-payment',
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent], 
  providers: [DecimalPipe]
})
export class PaymentComponent {
  cart = [
    { name: 'Produk A', price: 100000, qty: 2, image: 'https://via.placeholder.com/80' },
    { name: 'Produk B', price: 150000, qty: 1, image: 'https://via.placeholder.com/80' }
  ];

  paymentMethod: string = '';
  address: string = '';
  isAgreed: boolean = false;

  constructor(private decimalPipe: DecimalPipe) {}

  get totalPrice(): number {
    return this.cart.reduce((sum, item) => sum + item.price * item.qty, 0);
  }

  get formattedTotalPrice(): string {
    return this.decimalPipe.transform(this.totalPrice, '1.0-0') || '0';
  }

  get canConfirm(): boolean {
    return this.paymentMethod !== '' && this.address.trim() !== '' && this.isAgreed;
  }

  confirmOrder() {
    if (this.paymentMethod === 'transfer') {
      alert('Silakan lakukan transfer ke rekening kami.');
    } else if (this.paymentMethod === 'cod') {
      alert('Pesanan Anda akan dikonfirmasi untuk COD.');
    }
  }
}
