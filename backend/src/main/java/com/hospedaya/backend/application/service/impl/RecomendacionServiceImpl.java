package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.application.service.base.RecomendacionService;
import com.hospedaya.backend.domain.entity.Alojamiento;
import com.hospedaya.backend.domain.entity.Reserva;
import com.hospedaya.backend.infraestructure.repository.AlojamientoRepository;
import com.hospedaya.backend.infraestructure.repository.ReservaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecomendacionServiceImpl implements RecomendacionService {

    private final ReservaRepository reservaRepository;
    private final AlojamientoRepository alojamientoRepository;

    public RecomendacionServiceImpl(ReservaRepository reservaRepository, AlojamientoRepository alojamientoRepository) {
        this.reservaRepository = reservaRepository;
        this.alojamientoRepository = alojamientoRepository;
    }

    @Override
    public List<Alojamiento> recomendarPorUsuario(Long usuarioId, int limit) {
        if (usuarioId == null) return Collections.emptyList();
        int top = (limit <= 0) ? 8 : limit;

        // Historial del usuario
        List<Reserva> historial = reservaRepository.findByUsuarioId(usuarioId);
        Set<Long> yaReservados = historial.stream()
                .filter(r -> r.getAlojamiento() != null && r.getAlojamiento().getId() != null)
                .map(r -> r.getAlojamiento().getId())
                .collect(Collectors.toSet());

        // Extraer términos de ubicación (ciudad/barrio) desde direccion
        Map<String, Integer> terminos = new HashMap<>();
        for (Reserva r : historial) {
            if (r.getAlojamiento() == null) continue;
            String dir = r.getAlojamiento().getDireccion();
            String term = extraerCiudad(dir);
            if (term != null && !term.isBlank()) {
                terminos.merge(term, 1, Integer::sum);
            }
        }

        // Ordenar términos por frecuencia
        List<String> orden = terminos.entrySet().stream()
                .sorted((a,b) -> Integer.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        LinkedHashMap<Long, Alojamiento> resultado = new LinkedHashMap<>();

        // Buscar por términos más frecuentes
        for (String t : orden) {
            if (resultado.size() >= top) break;
            List<Alojamiento> candidatos = alojamientoRepository.findByDireccionContainingIgnoreCase(t);
            for (Alojamiento a : candidatos) {
                if (a == null || a.getId() == null) continue;
                if (yaReservados.contains(a.getId())) continue;
                if (!resultado.containsKey(a.getId())) {
                    resultado.put(a.getId(), a);
                    if (resultado.size() >= top) break;
                }
            }
        }

        // Si aún faltan, completar con otros alojamientos no reservados recientemente
        if (resultado.size() < top) {
            for (Alojamiento a : alojamientoRepository.findAll()) {
                if (a == null || a.getId() == null) continue;
                if (yaReservados.contains(a.getId())) continue;
                if (!resultado.containsKey(a.getId())) {
                    resultado.put(a.getId(), a);
                    if (resultado.size() >= top) break;
                }
            }
        }

        return new ArrayList<>(resultado.values());
    }

    private String extraerCiudad(String direccion) {
        if (direccion == null) return null;
        String s = direccion.trim();
        if (s.isEmpty()) return null;
        // Heurística: último segmento separado por coma
        String[] partes = s.split(",");
        String candidato = partes[partes.length - 1].trim();
        if (candidato.length() >= 3) return candidato;
        // fallback: primer token
        int sp = s.indexOf(' ');
        return sp > 0 ? s.substring(0, sp) : s;
    }
}
