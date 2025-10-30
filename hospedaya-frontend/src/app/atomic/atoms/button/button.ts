import { Component } from '@angular/core';
import {NgClass} from '@angular/common';

@Component({
  selector: 'app-button',
  templateUrl: './button.html',
  imports: [
    NgClass
  ],
  styleUrls: ['./button.scss']
})
export class ButtonComponent {
  type: 'primary' | 'secondary' | 'outline' = 'primary';
}
