import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar.component';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css']
})
export class ProductDetailComponent {
  @Input() product = {
    id: 1,
    name: 'Gaming Laptop',
    description: 'A high-performance laptop designed for gaming and productivity.',
    price: 15000000,
    stock: 10,
    category: 'Electronics',
    image: '/images/product.jpg'
  };

  addToCart() {
    console.log(`${this.product.name} added to cart!`);
  }
}
