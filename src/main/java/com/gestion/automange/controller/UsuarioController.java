package com.gestion.automange.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.gestion.automange.config.JwtProvider;
import com.gestion.automange.model.Orden;
import com.gestion.automange.model.Usuario;
import com.gestion.automange.service.IOrdenService;
import com.gestion.automange.service.IUsuarioService;


@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    private final Logger LOGGER = LoggerFactory.getLogger(UsuarioController.class);
    private final IUsuarioService usuarioService;
    private final IOrdenService ordenService;

    public UsuarioController(IUsuarioService usuarioService, IOrdenService ordenService, JwtProvider jwtProvider) {
        this.usuarioService = usuarioService;
        this.ordenService = ordenService;
    }


    // Obtener las compras de un usuario autenticado
    @GetMapping("/compras")
    public ResponseEntity<?> compras(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");
        }

        String email = userDetails.getUsername();
        Usuario usuario = usuarioService.findByEmail(email).orElse(null);

        if (usuario == null) {
            LOGGER.warn("Usuario no encontrado para obtener compras.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        List<Orden> compras = ordenService.findByUsuario(usuario);
        return ResponseEntity.ok(compras);
    }

    // Obtener el detalle de una compra
    @GetMapping("/detalle/{id}")
    public ResponseEntity<?> detalleCompra(@PathVariable Integer id) {
        LOGGER.info("Consultando detalle de compra, orden ID: {}", id);

        Optional<Orden> ordenOptional = ordenService.findById(id);
        if (ordenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Orden no encontrada");
        }

        return ResponseEntity.ok(ordenOptional.get());
    }
}
