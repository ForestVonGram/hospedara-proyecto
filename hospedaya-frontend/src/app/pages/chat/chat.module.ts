import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { ChatComponent } from './chat.component';

const routes: Routes = [
  { path: '', component: ChatComponent },
  { path: ':alojamientoId/:usuarioId', component: ChatComponent }
];

@NgModule({
  declarations: [
    ChatComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule.forChild(routes)
  ]
})
export class ChatModule { }
