import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgxPaginationModule } from 'ngx-pagination';
import { ProductCardComponent } from '../product-card/product-card.component';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ProductCardComponent, NgxPaginationModule],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent {
  products = [
    { id: 1, name: 'Gaming Laptop', price: 1000, image: 'images/product.jpg' },
    { id: 2, name: 'Smartphone Pro', price: 533, image: 'images/product.jpg' },
    { id: 3, name: 'Wireless Headphones', price: 133, image: 'images/product.jpg' },
    { id: 4, name: 'Smartwatch', price: 167, image: 'images/product.jpg' },
    { id: 5, name: 'DSLR Camera', price: 800, image: 'images/product.jpg' },
    { id: 6, name: 'Mechanical Keyboard', price: 100, image: 'images/product.jpg' },
    { id: 7, name: 'Gaming Mouse', price: 60, image: 'images/product.jpg' },
    { id: 8, name: '4K Monitor', price: 333, image: 'images/product.jpg' },
    { id: 9, name: 'Bluetooth Speaker', price: 67, image: 'images/product.jpg' },
    { id: 10, name: 'Pro Tablet', price: 500, image: 'images/product.jpg' },
    { id: 11, name: 'Power Bank', price: 20, image: 'images/product.jpg' },
    { id: 12, name: 'WiFi 6 Router', price: 100, image: 'images/product.jpg' },
    { id: 13, name: '128GB Flash Drive', price: 13, image: 'images/product.jpg' },
    { id: 14, name: '1TB NVMe SSD', price: 133, image: 'images/product.jpg' },
    { id: 15, name: 'Laptop Cooling Pad', price: 33, image: 'images/product.jpg' },
    { id: 16, name: '55" Smart TV', price: 567, image: 'images/product.jpg' },
    { id: 17, name: 'Gaming Chair', price: 167, image: 'images/product.jpg' },
    { id: 18, name: '4K Drone', price: 1000, image: 'images/product.jpg' },
    { id: 19, name: 'VR Headset', price: 667, image: 'images/product.jpg' },
    { id: 20, name: 'Streaming Microphone', price: 113, image: 'images/product.jpg' }
  ];

  filteredProducts = [...this.products];
  currentPage = 1;
  itemsPerPage = 10;
  sortOption = ''; 

  sortProducts() {
    if (this.sortOption === 'name-asc') {
      this.filteredProducts.sort((a, b) => a.name.localeCompare(b.name));
    } else if (this.sortOption === 'name-desc') {
      this.filteredProducts.sort((a, b) => b.name.localeCompare(a.name));
    } else if (this.sortOption === 'price-asc') {
      this.filteredProducts.sort((a, b) => a.price - b.price);
    } else if (this.sortOption === 'price-desc') {
      this.filteredProducts.sort((a, b) => b.price - a.price);
    }
  }

  filterProducts(searchTerm: string) {
    this.filteredProducts = this.products.filter(product =>
      product.name.toLowerCase().includes(searchTerm.toLowerCase())
    );
  }
}
