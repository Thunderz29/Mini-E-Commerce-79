import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-home',
  imports: [MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  products = [
    {
      name: 'Smartphone XYZ',
      price: 3000000,
      description: 'Smartphone dengan kamera terbaik dan baterai tahan lama.',
      image: 'assets/products/smartphone.jpg'
    },
    {
      name: 'Laptop ABC',
      price: 7500000,
      description: 'Laptop performa tinggi untuk pekerjaan dan gaming.',
      image: 'assets/products/laptop.jpg'
    },
    {
      name: 'Headphone Wireless',
      price: 500000,
      description: 'Headphone dengan suara jernih dan bass yang mantap.',
      image: 'assets/products/headphone.jpg'
    }
  ];

  addToCart(product: any) {
    alert(`${product.name} added to cart!`);
  }
}
